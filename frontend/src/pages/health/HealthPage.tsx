import { useEffect, useState } from 'react';
import { Plus, Search, Edit, Trash2, AlertCircle } from 'lucide-react';
import { HealthRecord } from '../../types/health.types';
import { Cow } from '../../types/cow.types';
import { healthService } from '../../services/healthService';
import { cowService } from '../../services/cowService';
import { handleApiError } from '../../utils/errorHandler';
import { formatDate } from '../../utils/formatters';
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
import { HealthRecordForm } from '../../components/forms/HealthRecordForm';

export function HealthPage() {
  const [records, setRecords] = useState<HealthRecord[]>([]);
  const [cows, setCows] = useState<Cow[]>([]);
  const [withdrawals, setWithdrawals] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);
  const [cowsLoading, setCowsLoading] = useState(true);
  const [searchQuery, setSearchQuery] = useState('');
  const [createDialogOpen, setCreateDialogOpen] = useState(false);
  const [editDialogOpen, setEditDialogOpen] = useState(false);
  const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);
  const [selectedRecord, setSelectedRecord] = useState<HealthRecord | null>(null);
  const [deleting, setDeleting] = useState(false);

  useEffect(() => {
    fetchData();
  }, []);

  const fetchData = async () => {
    setLoading(true);
    setCowsLoading(true);
    try {
      // Fetch cows first as they're required for the form
      const cowsData = await cowService.getCows({ status: 'ACTIVE' });
      setCows(cowsData.content || []);
      setCowsLoading(false);
      
      // Then fetch records and withdrawals in parallel
      const [recordsData, withdrawalsData] = await Promise.all([
        healthService.getHealthRecords({ page: 0, size: 100 }),
        healthService.getActiveWithdrawals(),
      ]);
      setRecords(recordsData.content || []);
      setWithdrawals(withdrawalsData || []);
    } catch (error) {
      const errorMessage = handleApiError(error);
      console.error('Error fetching health data:', error);
      toast.error(errorMessage);
      // Set empty arrays on error to prevent undefined issues
      setCows([]);
      setRecords([]);
      setWithdrawals([]);
      setCowsLoading(false);
    } finally {
      setLoading(false);
    }
  };

  const handleCreateSuccess = () => {
    setCreateDialogOpen(false);
    fetchData();
    toast.success('Health record created successfully');
  };

  const handleEditSuccess = () => {
    setEditDialogOpen(false);
    setSelectedRecord(null);
    fetchData();
    toast.success('Health record updated successfully');
  };

  const handleDelete = async () => {
    if (!selectedRecord) return;

    setDeleting(true);
    try {
      await healthService.deleteHealthRecord(selectedRecord.id);
      toast.success('Health record deleted successfully');
      setDeleteDialogOpen(false);
      setSelectedRecord(null);
      fetchData();
    } catch (error) {
      toast.error(handleApiError(error));
    } finally {
      setDeleting(false);
    }
  };

  const openEditDialog = (record: HealthRecord) => {
    setSelectedRecord(record);
    setEditDialogOpen(true);
  };

  const openDeleteDialog = (record: HealthRecord) => {
    setSelectedRecord(record);
    setDeleteDialogOpen(true);
  };

  const filteredRecords = records.filter((record) => {
    const cow = cows.find((c) => c.id === record.cowId);
    const cowTag = cow?.tagId || '';
    return (
      cowTag.toLowerCase().includes(searchQuery.toLowerCase()) ||
      record.recordType.toLowerCase().includes(searchQuery.toLowerCase())
    );
  });

  const getCowTag = (cowId: number) => {
    const cow = cows.find((c) => c.id === cowId);
    return cow?.tagId || 'Unknown';
  };

  const getDaysRemaining = (endDate: string) => {
    const end = new Date(endDate);
    const today = new Date();
    const diff = Math.ceil((end.getTime() - today.getTime()) / (1000 * 60 * 60 * 24));
    return diff;
  };

  return (
    <div className="p-6 space-y-6">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-semibold">Health Management</h1>
          <p className="text-muted-foreground mt-1">Track health records and withdrawal periods</p>
        </div>
        <Button 
          onClick={() => setCreateDialogOpen(true)}
          disabled={cowsLoading || cows.length === 0}
        >
          <Plus className="h-4 w-4 mr-2" />
          {cowsLoading ? 'Loading...' : 'Create Health Record'}
        </Button>
      </div>

      {/* No cows warning */}
      {!cowsLoading && cows.length === 0 && (
        <Card className="border-yellow-200 bg-yellow-50">
          <CardContent className="pt-6">
            <div className="flex items-center gap-2 text-yellow-800">
              <AlertCircle className="h-5 w-5" />
              <p>No cows registered. Please register cows in the Livestock page before creating health records.</p>
            </div>
          </CardContent>
        </Card>
      )}

      {/* Active Withdrawals */}
      {withdrawals.length > 0 && (
        <Card className="border-orange-200 bg-orange-50">
          <CardHeader>
            <CardTitle className="flex items-center gap-2 text-orange-800">
              <AlertCircle className="h-5 w-5" />
              Active Withdrawal Periods
            </CardTitle>
          </CardHeader>
          <CardContent>
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead>Cow Tag ID</TableHead>
                  <TableHead>Medication</TableHead>
                  <TableHead>End Date</TableHead>
                  <TableHead>Days Remaining</TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                {withdrawals.map((withdrawal) => {
                  const daysRemaining = getDaysRemaining(withdrawal.withdrawalEndDate);
                  return (
                    <TableRow key={withdrawal.cowId}>
                      <TableCell className="font-medium">{withdrawal.cowTagId}</TableCell>
                      <TableCell>{withdrawal.medication}</TableCell>
                      <TableCell>{formatDate(withdrawal.withdrawalEndDate)}</TableCell>
                      <TableCell>
                        <Badge
                          className={
                            daysRemaining <= 3
                              ? 'bg-red-100 text-red-800'
                              : 'bg-yellow-100 text-yellow-800'
                          }
                        >
                          {daysRemaining} days
                        </Badge>
                      </TableCell>
                    </TableRow>
                  );
                })}
              </TableBody>
            </Table>
          </CardContent>
        </Card>
      )}

      {/* Health Records */}
      <Card>
        <CardHeader>
          <CardTitle>Health Records</CardTitle>
          <div className="flex gap-4 mt-4">
            <div className="flex-1 relative">
              <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-muted-foreground w-4 h-4" />
              <Input
                placeholder="Search by cow tag ID or record type..."
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
          ) : filteredRecords.length === 0 ? (
            <div className="text-center py-8 text-muted-foreground">
              No health records found. Create your first health record to get started.
            </div>
          ) : (
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead>Date</TableHead>
                  <TableHead>Cow Tag ID</TableHead>
                  <TableHead>Type</TableHead>
                  <TableHead>Description</TableHead>
                  <TableHead>Veterinarian</TableHead>
                  <TableHead>Cost</TableHead>
                  <TableHead>Actions</TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                {filteredRecords.map((record) => (
                  <TableRow key={record.id}>
                    <TableCell>{formatDate(record.date)}</TableCell>
                    <TableCell className="font-medium">{getCowTag(record.cowId)}</TableCell>
                    <TableCell>
                      <Badge variant="outline">{record.recordType}</Badge>
                    </TableCell>
                    <TableCell>{record.description}</TableCell>
                    <TableCell>{record.veterinarianName || '-'}</TableCell>
                    <TableCell>${record.cost?.toFixed(2) || '0.00'}</TableCell>
                    <TableCell>
                      <div className="flex gap-2">
                        <Button
                          variant="ghost"
                          size="sm"
                          onClick={() => openEditDialog(record)}
                        >
                          <Edit className="h-4 w-4" />
                        </Button>
                        <Button
                          variant="ghost"
                          size="sm"
                          onClick={() => openDeleteDialog(record)}
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
        <DialogContent className="max-w-2xl max-h-[90vh] overflow-y-auto">
          <DialogHeader>
            <DialogTitle>Create Health Record</DialogTitle>
          </DialogHeader>
          <HealthRecordForm
            cows={cows}
            onSuccess={handleCreateSuccess}
            onCancel={() => setCreateDialogOpen(false)}
          />
        </DialogContent>
      </Dialog>

      {/* Edit Dialog */}
      <Dialog open={editDialogOpen} onOpenChange={setEditDialogOpen}>
        <DialogContent className="max-w-2xl max-h-[90vh] overflow-y-auto">
          <DialogHeader>
            <DialogTitle>Edit Health Record</DialogTitle>
          </DialogHeader>
          {selectedRecord && (
            <HealthRecordForm
              initialData={selectedRecord}
              cows={cows}
              onSuccess={handleEditSuccess}
              onCancel={() => {
                setEditDialogOpen(false);
                setSelectedRecord(null);
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
              This will permanently delete this health record. This action cannot be undone.
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
