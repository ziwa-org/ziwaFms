import { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { ArrowLeft, Edit, Trash2, Plus, Calendar, Activity, DollarSign } from 'lucide-react';
import { Cow, BreedingRecord } from '../../types/cow.types';
import { cowService } from '../../services/cowService';
import { productionService } from '../../services/productionService';
import { healthService } from '../../services/healthService';
import { ProductionRecord } from '../../types/production.types';
import { HealthRecord } from '../../types/health.types';
import { handleApiError } from '../../utils/errorHandler';
import { formatDate } from '../../utils/formatters';
import { Button } from '../../components/ui/button';
import { Card, CardContent, CardHeader, CardTitle } from '../../components/ui/card';
import { Badge } from '../../components/ui/badge';
import { toast } from 'sonner';
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
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
} from '../../components/ui/dialog';
import { CowForm } from '../../components/forms/CowForm';
import { BreedingRecordForm } from '../../components/forms/BreedingRecordForm';
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from '../../components/ui/table';

export function CowDetailsPage() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const [cow, setCow] = useState<Cow | null>(null);
  const [breedingRecords, setBreedingRecords] = useState<BreedingRecord[]>([]);
  const [productionRecords, setProductionRecords] = useState<ProductionRecord[]>([]);
  const [healthRecords, setHealthRecords] = useState<HealthRecord[]>([]);
  const [loading, setLoading] = useState(true);
  const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);
  const [editDialogOpen, setEditDialogOpen] = useState(false);
  const [breedingDialogOpen, setBreedingDialogOpen] = useState(false);
  const [deleting, setDeleting] = useState(false);

  useEffect(() => {
    if (id) {
      fetchCowDetails();
    }
  }, [id]);

  const fetchCowDetails = async () => {
    if (!id) return;
    
    setLoading(true);
    try {
      const [cowData, breedingData, productionData, healthData] = await Promise.all([
        cowService.getCowById(parseInt(id)),
        cowService.getBreedingRecords(parseInt(id)),
        productionService.getProductionRecords({ cowId: parseInt(id), page: 0, size: 5 }),
        healthService.getHealthRecords({ cowId: parseInt(id), page: 0, size: 5 }),
      ]);

      setCow(cowData);
      setBreedingRecords(breedingData);
      setProductionRecords(productionData.content);
      setHealthRecords(healthData.content);
    } catch (error) {
      toast.error(handleApiError(error));
      navigate('/livestock');
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async () => {
    if (!id) return;
    
    setDeleting(true);
    try {
      await cowService.deleteCow(parseInt(id));
      toast.success('Cow deleted successfully');
      navigate('/livestock');
    } catch (error) {
      toast.error(handleApiError(error));
    } finally {
      setDeleting(false);
      setDeleteDialogOpen(false);
    }
  };

  const handleEditSuccess = () => {
    setEditDialogOpen(false);
    fetchCowDetails();
    toast.success('Cow updated successfully');
  };

  const handleBreedingSuccess = () => {
    setBreedingDialogOpen(false);
    fetchCowDetails();
    toast.success('Breeding record added successfully');
  };

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'ACTIVE':
        return 'bg-green-500';
      case 'SOLD':
        return 'bg-blue-500';
      case 'DECEASED':
        return 'bg-gray-500';
      default:
        return 'bg-gray-500';
    }
  };

  if (loading) {
    return (
      <div className="p-6">
        <div className="animate-pulse space-y-4">
          <div className="h-8 bg-gray-200 rounded w-1/4"></div>
          <div className="h-64 bg-gray-200 rounded"></div>
        </div>
      </div>
    );
  }

  if (!cow) {
    return (
      <div className="p-6">
        <p>Cow not found</p>
      </div>
    );
  }

  return (
    <div className="p-6 space-y-6">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div className="flex items-center gap-4">
          <Button
            variant="ghost"
            size="icon"
            onClick={() => navigate('/livestock')}
          >
            <ArrowLeft className="h-5 w-5" />
          </Button>
          <div>
            <h1 className="text-3xl font-semibold">Cow Details</h1>
            <p className="text-muted-foreground">Tag ID: {cow.tagId}</p>
          </div>
        </div>
        <div className="flex gap-2">
          <Button
            variant="outline"
            onClick={() => setEditDialogOpen(true)}
          >
            <Edit className="h-4 w-4 mr-2" />
            Edit
          </Button>
          <Button
            variant="destructive"
            onClick={() => setDeleteDialogOpen(true)}
          >
            <Trash2 className="h-4 w-4 mr-2" />
            Delete
          </Button>
        </div>
      </div>

      {/* Cow Information */}
      <Card>
        <CardHeader>
          <CardTitle>Basic Information</CardTitle>
        </CardHeader>
        <CardContent>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div>
              <p className="text-sm text-muted-foreground">Tag ID</p>
              <p className="font-medium">{cow.tagId}</p>
            </div>
            <div>
              <p className="text-sm text-muted-foreground">Breed</p>
              <p className="font-medium">{cow.breed}</p>
            </div>
            <div>
              <p className="text-sm text-muted-foreground">Date of Birth</p>
              <p className="font-medium">{formatDate(cow.dateOfBirth)}</p>
            </div>
            <div>
              <p className="text-sm text-muted-foreground">Acquisition Date</p>
              <p className="font-medium">{formatDate(cow.acquisitionDate)}</p>
            </div>
            <div>
              <p className="text-sm text-muted-foreground">Status</p>
              <div className="flex items-center gap-2">
                <Badge className={getStatusColor(cow.status)}>{cow.status}</Badge>
                <Button
                  variant="outline"
                  size="sm"
                  onClick={() => setEditDialogOpen(true)}
                  className="text-xs"
                >
                  Change Status
                </Button>
              </div>
            </div>
          </div>
        </CardContent>
      </Card>

      {/* Breeding Records */}
      <Card>
        <CardHeader className="flex flex-row items-center justify-between">
          <CardTitle>Breeding Records</CardTitle>
          <Button
            size="sm"
            onClick={() => setBreedingDialogOpen(true)}
          >
            <Plus className="h-4 w-4 mr-2" />
            Add Record
          </Button>
        </CardHeader>
        <CardContent>
          {breedingRecords.length === 0 ? (
            <p className="text-muted-foreground text-center py-4">No breeding records</p>
          ) : (
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead>Breeding Date</TableHead>
                  <TableHead>Bull ID</TableHead>
                  <TableHead>Expected Calving</TableHead>
                  <TableHead>Notes</TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                {breedingRecords.map((record) => (
                  <TableRow key={record.id}>
                    <TableCell>{formatDate(record.breedingDate)}</TableCell>
                    <TableCell>{record.bullId}</TableCell>
                    <TableCell>{formatDate(record.expectedCalvingDate)}</TableCell>
                    <TableCell>{record.notes || '-'}</TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          )}
        </CardContent>
      </Card>

      {/* Recent Production Records */}
      <Card>
        <CardHeader>
          <CardTitle className="flex items-center gap-2">
            <Activity className="h-5 w-5" />
            Recent Production Records
          </CardTitle>
        </CardHeader>
        <CardContent>
          {productionRecords.length === 0 ? (
            <p className="text-muted-foreground text-center py-4">No production records</p>
          ) : (
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead>Date</TableHead>
                  <TableHead>Morning (L)</TableHead>
                  <TableHead>Evening (L)</TableHead>
                  <TableHead>Total (L)</TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                {productionRecords.map((record) => (
                  <TableRow key={record.id}>
                    <TableCell>{formatDate(record.date)}</TableCell>
                    <TableCell>{record.morningQuantity}</TableCell>
                    <TableCell>{record.eveningQuantity}</TableCell>
                    <TableCell className="font-medium">{record.totalQuantity}</TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          )}
        </CardContent>
      </Card>

      {/* Recent Health Records */}
      <Card>
        <CardHeader>
          <CardTitle className="flex items-center gap-2">
            <Calendar className="h-5 w-5" />
            Recent Health Records
          </CardTitle>
        </CardHeader>
        <CardContent>
          {healthRecords.length === 0 ? (
            <p className="text-muted-foreground text-center py-4">No health records</p>
          ) : (
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead>Date</TableHead>
                  <TableHead>Type</TableHead>
                  <TableHead>Description</TableHead>
                  <TableHead>Veterinarian</TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                {healthRecords.map((record) => (
                  <TableRow key={record.id}>
                    <TableCell>{formatDate(record.date)}</TableCell>
                    <TableCell>{record.recordType}</TableCell>
                    <TableCell>{record.description}</TableCell>
                    <TableCell>{record.veterinarianName || '-'}</TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          )}
        </CardContent>
      </Card>

      {/* Delete Confirmation Dialog */}
      <AlertDialog open={deleteDialogOpen} onOpenChange={setDeleteDialogOpen}>
        <AlertDialogContent>
          <AlertDialogHeader>
            <AlertDialogTitle>Are you sure?</AlertDialogTitle>
            <AlertDialogDescription>
              This will permanently delete cow {cow.tagId} and all associated records.
              This action cannot be undone.
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

      {/* Edit Dialog */}
      <Dialog open={editDialogOpen} onOpenChange={setEditDialogOpen}>
        <DialogContent className="max-w-2xl">
          <DialogHeader>
            <DialogTitle>Edit Cow</DialogTitle>
          </DialogHeader>
          <CowForm
            initialData={cow}
            onSuccess={handleEditSuccess}
            onCancel={() => setEditDialogOpen(false)}
          />
        </DialogContent>
      </Dialog>

      {/* Breeding Record Dialog */}
      <Dialog open={breedingDialogOpen} onOpenChange={setBreedingDialogOpen}>
        <DialogContent className="max-w-2xl">
          <DialogHeader>
            <DialogTitle>Add Breeding Record</DialogTitle>
          </DialogHeader>
          <BreedingRecordForm
            cowId={cow.id}
            onSuccess={handleBreedingSuccess}
            onCancel={() => setBreedingDialogOpen(false)}
          />
        </DialogContent>
      </Dialog>
    </div>
  );
}
