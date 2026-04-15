import { useEffect, useState } from 'react';
import { Plus, Search, Edit, Trash2, TrendingUp, TrendingDown } from 'lucide-react';
import { Transaction } from '../../types/financial.types';
import { financialService } from '../../services/financialService';
import { handleApiError } from '../../utils/errorHandler';
import { formatDate, formatCurrency } from '../../utils/formatters';
import { Button } from '../../components/ui/button';
import { Input } from '../../components/ui/input';
import { Card, CardContent, CardHeader, CardTitle } from '../../components/ui/card';
import { Badge } from '../../components/ui/badge';
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from '../../components/ui/table';
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
} from '../../components/ui/dialog';
import {
  AlertDialog,
  AlertDialogAction,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
} from '../../components/ui/alert-dialog';
import { toast } from 'sonner';
import { TransactionForm } from '../../components/forms/TransactionForm';

export function FinancialPage() {
  const [transactions, setTransactions] = useState<Transaction[]>([]);
  const [profitLoss, setProfitLoss] = useState<any>(null);
  const [loading, setLoading] = useState(true);
  const [searchQuery, setSearchQuery] = useState('');
  const [createDialogOpen, setCreateDialogOpen] = useState(false);
  const [editDialogOpen, setEditDialogOpen] = useState(false);
  const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);
  const [selectedTransaction, setSelectedTransaction] = useState<Transaction | null>(null);
  const [deleting, setDeleting] = useState(false);

  useEffect(() => {
    fetchData();
  }, []);

  const fetchData = async () => {
    setLoading(true);
    try {
      // Get current month date range
      const now = new Date();
      const startDate = new Date(now.getFullYear(), now.getMonth(), 1).toISOString().split('T')[0];
      const endDate = new Date(now.getFullYear(), now.getMonth() + 1, 0).toISOString().split('T')[0];

      const [transactionsData, profitLossData] = await Promise.all([
        financialService.getTransactions({ page: 0, size: 100 }),
        financialService.getProfitLoss(startDate, endDate).catch(() => null), // Don't fail if no data
      ]);
      setTransactions(transactionsData.content);
      setProfitLoss(profitLossData);
    } catch (error) {
      const errorMessage = handleApiError(error);
      console.error('Error fetching financial data:', errorMessage);
      toast.error(errorMessage);
    } finally {
      setLoading(false);
    }
  };

  const handleCreateSuccess = () => {
    setCreateDialogOpen(false);
    fetchData();
    toast.success('Transaction created successfully');
  };

  const handleEditSuccess = () => {
    setEditDialogOpen(false);
    setSelectedTransaction(null);
    fetchData();
    toast.success('Transaction updated successfully');
  };

  const handleDelete = async () => {
    if (!selectedTransaction) return;

    setDeleting(true);
    try {
      await financialService.deleteTransaction(selectedTransaction.id);
      toast.success('Transaction deleted successfully');
      setDeleteDialogOpen(false);
      setSelectedTransaction(null);
      fetchData();
    } catch (error) {
      toast.error(handleApiError(error));
    } finally {
      setDeleting(false);
    }
  };

  const openEditDialog = (transaction: Transaction) => {
    setSelectedTransaction(transaction);
    setEditDialogOpen(true);
  };

  const openDeleteDialog = (transaction: Transaction) => {
    setSelectedTransaction(transaction);
    setDeleteDialogOpen(true);
  };

  const filteredTransactions = transactions.filter((transaction) =>
    transaction.description.toLowerCase().includes(searchQuery.toLowerCase()) ||
    transaction.category.toLowerCase().includes(searchQuery.toLowerCase())
  );

  const getTypeBadge = (type: string) => {
    return type === 'INCOME' ? (
      <Badge className="bg-green-100 text-green-800">Income</Badge>
    ) : (
      <Badge className="bg-red-100 text-red-800">Expense</Badge>
    );
  };

  return (
    <div className="p-6 space-y-6">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-semibold">Financial Management</h1>
          <p className="text-muted-foreground mt-1">Track income and expenses</p>
        </div>
        <Button onClick={() => setCreateDialogOpen(true)}>
          <Plus className="h-4 w-4 mr-2" />
          Record Transaction
        </Button>
      </div>

      {/* Financial Summary */}
      {profitLoss && (
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          <Card>
            <CardHeader className="pb-2">
              <CardTitle className="text-sm font-medium text-muted-foreground">
                Total Income
              </CardTitle>
            </CardHeader>
            <CardContent>
              <div className="flex items-center gap-2">
                <TrendingUp className="h-5 w-5 text-green-500" />
                <span className="text-2xl font-bold text-green-600">
                  {formatCurrency(profitLoss.totalIncome)}
                </span>
              </div>
            </CardContent>
          </Card>

          <Card>
            <CardHeader className="pb-2">
              <CardTitle className="text-sm font-medium text-muted-foreground">
                Total Expenses
              </CardTitle>
            </CardHeader>
            <CardContent>
              <div className="flex items-center gap-2">
                <TrendingDown className="h-5 w-5 text-red-500" />
                <span className="text-2xl font-bold text-red-600">
                  {formatCurrency(profitLoss.totalExpenses)}
                </span>
              </div>
            </CardContent>
          </Card>

          <Card>
            <CardHeader className="pb-2">
              <CardTitle className="text-sm font-medium text-muted-foreground">
                Net Profit
              </CardTitle>
            </CardHeader>
            <CardContent>
              <div className="flex items-center gap-2">
                <span
                  className={`text-2xl font-bold ${
                    profitLoss.netProfit >= 0 ? 'text-green-600' : 'text-red-600'
                  }`}
                >
                  {formatCurrency(profitLoss.netProfit)}
                </span>
              </div>
            </CardContent>
          </Card>
        </div>
      )}

      {/* Transactions */}
      <Card>
        <CardHeader>
          <CardTitle>Transactions</CardTitle>
          <div className="flex gap-4 mt-4">
            <div className="flex-1 relative">
              <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-muted-foreground w-4 h-4" />
              <Input
                placeholder="Search by description or category..."
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
                className="pl-10"
              />
            </div>
          </div>
        </CardHeader>
        <CardContent>
          {loading ? (
            <div className="text-center py-8">Loading...</div>
          ) : filteredTransactions.length === 0 ? (
            <div className="text-center py-8 text-muted-foreground">
              No transactions found. Record your first transaction to get started.
            </div>
          ) : (
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead>Date</TableHead>
                  <TableHead>Type</TableHead>
                  <TableHead>Category</TableHead>
                  <TableHead>Description</TableHead>
                  <TableHead>Amount</TableHead>
                  <TableHead>Reference</TableHead>
                  <TableHead>Actions</TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                {filteredTransactions.map((transaction) => (
                  <TableRow key={transaction.id}>
                    <TableCell>{formatDate(transaction.date)}</TableCell>
                    <TableCell>{getTypeBadge(transaction.type)}</TableCell>
                    <TableCell>{transaction.category}</TableCell>
                    <TableCell>{transaction.description}</TableCell>
                    <TableCell className="font-semibold">
                      {formatCurrency(transaction.amount)}
                    </TableCell>
                    <TableCell>{transaction.referenceId || '-'}</TableCell>
                    <TableCell>
                      <div className="flex gap-2">
                        <Button
                          variant="ghost"
                          size="sm"
                          onClick={() => openEditDialog(transaction)}
                        >
                          <Edit className="h-4 w-4" />
                        </Button>
                        <Button
                          variant="ghost"
                          size="sm"
                          onClick={() => openDeleteDialog(transaction)}
                        >
                          <Trash2 className="h-4 w-4 text-red-500" />
                        </Button>
                      </div>
                    </TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          )}
        </CardContent>
      </Card>

      {/* Create Dialog */}
      <Dialog open={createDialogOpen} onOpenChange={setCreateDialogOpen}>
        <DialogContent className="max-w-2xl">
          <DialogHeader>
            <DialogTitle>Record Transaction</DialogTitle>
          </DialogHeader>
          <TransactionForm
            onSuccess={handleCreateSuccess}
            onCancel={() => setCreateDialogOpen(false)}
          />
        </DialogContent>
      </Dialog>

      {/* Edit Dialog */}
      <Dialog open={editDialogOpen} onOpenChange={setEditDialogOpen}>
        <DialogContent className="max-w-2xl">
          <DialogHeader>
            <DialogTitle>Edit Transaction</DialogTitle>
          </DialogHeader>
          {selectedTransaction && (
            <TransactionForm
              initialData={selectedTransaction}
              onSuccess={handleEditSuccess}
              onCancel={() => {
                setEditDialogOpen(false);
                setSelectedTransaction(null);
              }}
            />
          )}
        </DialogContent>
      </Dialog>

      {/* Delete Confirmation Dialog */}
      <AlertDialog open={deleteDialogOpen} onOpenChange={setDeleteDialogOpen}>
        <AlertDialogContent>
          <AlertDialogHeader>
            <AlertDialogTitle>Are you sure?</AlertDialogTitle>
            <AlertDialogDescription>
              This will permanently delete this transaction. This action cannot be undone.
            </AlertDialogDescription>
          </AlertDialogHeader>
          <AlertDialogFooter>
            <AlertDialogCancel>Cancel</AlertDialogCancel>
            <AlertDialogAction
              onClick={handleDelete}
              disabled={deleting}
              className="bg-red-600 hover:bg-red-700"
            >
              {deleting ? 'Deleting...' : 'Delete'}
            </AlertDialogAction>
          </AlertDialogFooter>
        </AlertDialogContent>
      </AlertDialog>
    </div>
  );
}
