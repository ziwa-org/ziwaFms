import { useEffect, useState } from 'react';
import { Plus, Search, Edit, Trash2 } from 'lucide-react';
import { ProductionRecord } from '../../types/production.types';
import { Cow } from '../../types/cow.types';
import { productionService } from '../../services/productionService';
import { cowService } from '../../services/cowService';
import { handleApiError } from '../../utils/errorHandler';
import { formatDate } from '../../utils/formatters';
import { Button } from '../../components/ui/button';
import { Input } from '../../components/ui/input';
import { Card, CardContent, CardHeader, CardTitle } from '../../components/ui/card';
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
import { ProductionRecordForm } from '../../components/forms/ProductionRecordForm';

export function ProductionPage() {
  const [records, setRecords] = useState<ProductionRecord[]>([]);
  const [cows, setCows] = useState<Cow[]>([]);
  const [loading, setLoading] = useState(true);
  const [searchQuery, setSearchQuery] = useState('');
  const [createDialogOpen, setCreateDialogOpen] = useState(false);
  const [editDialogOpen, setEditDialogOpen] = useState(false);
  const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);
  const [selectedRecord, setSelectedRecord] = useState<ProductionRecord | null>(null);
  const [deleting, setDeleting] = useState(false);

  useEffect(() => {
    fetchData();
  }, []);

  const fetchData = async () => {
    setLoading(true);
    try {
      const [recordsData, cowsData] = await Promise.all([
        productionService.getProductionRecords({ page: 0, size: 100 }),
        cowService.getCows({ status: 'ACTIVE' }),
      ]);
      setRecords(recordsData.content);
      setCows(cowsData.content);
    } catch (error) {
      toast.error(handleApiError(error));
    } finally {
      setLoading(false);
    }
  };

  const handleCreateSuccess = () => {
    setCreateDialogOpen(false);
    fetchData();
    toast.success('Production record created successfully');
  };

  const handleEditSuccess = () => {
    setEditDialogOpen(false);
    setSelectedRecord(null);
    fetchData();
    toast.success('Production record updated successfully');
  };

  const handleDelete = async () => {
    if (!selectedRecord) return;

    setDeleting(true);
    try {
      await productionService.deleteProductionRecord(selectedRecord.id);
      toast.success('Production record deleted successfully');
      setDeleteDialogOpen(false);
      setSelectedRecord(null);
      fetchData();
    } catch (error) {
      toast.error(handleApiError(error));
    } finally {
      setDeleting(false);
    }
  };

  const openEditDialog = (record: ProductionRecord) => {
    setSelectedRecord(record);
    setEditDialogOpen(true);
  };

  const openDeleteDialog = (record: ProductionRecord) => {
    setSelectedRecord(record);
    setDeleteDialogOpen(true);
  };

  const filteredRecords = records.filter((record) => {
    const cow = cows.find((c) => c.id === record.cowId);
    const cowTag = cow?.tagId || '';
    return cowTag.toLowerCase().includes(searchQuery.toLowerCase());
  });

  const getCowTag = (cowId: number) => {
    const cow = cows.find((c) => c.id === cowId);
    return cow?.tagId || 'Unknown';
  };

  return (
    <div className="p-6 space-y-6">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-semibold">Production Management</h1>
          <p className="text-muted-foreground mt-1">Track daily milk production</p>
        </div>
        <Button onClick={() => setCreateDialogOpen(true)}>
          <Plus className="h-4 w-4 mr-2" />
          Record Production
        </Button>
      </div>

      {/* Production Records */}
      <Card>
        <CardHeader>
          <CardTitle>Production Records</CardTitle>
          <div className="flex gap-4 mt-4">
            <div className="flex-1 relative">
              <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-muted-foreground w-4 h-4" />
              <Input
                placeholder="Search by cow tag ID..."
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
              No production records found. Record your first production to get started.
            </div>
          ) : (
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead>Date</TableHead>
                  <TableHead>Cow Tag ID</TableHead>
                  <TableHead>Morning (L)</TableHead>
                  <TableHead>Evening (L)</TableHead>
                  <TableHead>Total (L)</TableHead>
                  <TableHead>Notes</TableHead>
                  <TableHead>Actions</TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                {filteredRecords.map((record) => (
                  <TableRow key={record.id}>
                    <TableCell>{formatDate(record.date)}</TableCell>
                    <TableCell className="font-medium">{getCowTag(record.cowId)}</TableCell>
                    <TableCell>{record.morningQuantity}</TableCell>
                    <TableCell>{record.eveningQuantity}</TableCell>
                    <TableCell className="font-semibold">{record.totalQuantity}</TableCell>
                    <TableCell>{record.notes || '-'}</TableCell>
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
        <DialogContent className="max-w-2xl">
          <DialogHeader>
            <DialogTitle>Record Production</DialogTitle>
          </DialogHeader>
          <ProductionRecordForm
            cows={cows}
            onSuccess={handleCreateSuccess}
            onCancel={() => setCreateDialogOpen(false)}
          />
        </DialogContent>
      </Dialog>

      {/* Edit Dialog */}
      <Dialog open={editDialogOpen} onOpenChange={setEditDialogOpen}>
        <DialogContent className="max-w-2xl">
          <DialogHeader>
            <DialogTitle>Edit Production Record</DialogTitle>
          </DialogHeader>
          {selectedRecord && (
            <ProductionRecordForm
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
              This will permanently delete this production record. This action cannot be undone.
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
