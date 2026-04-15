import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Plus, Search, MoreVertical } from 'lucide-react';
import { Button } from '../../components/ui/button';
import { Input } from '../../components/ui/input';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '../../components/ui/card';
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from '../../components/ui/table';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '../../components/ui/select';
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from '../../components/ui/dropdown-menu';
import { Badge } from '../../components/ui/badge';
import { Dialog, DialogContent, DialogDescription, DialogHeader, DialogTitle, DialogTrigger } from '../../components/ui/dialog';
import { Label } from '../../components/ui/label';
import { formatDate } from '../../utils/formatters';
import { cowService } from '../../services/cowService';
import { Cow, CowStatus } from '../../types/cow.types';
import { handleApiError } from '../../utils/errorHandler';
import { toast } from 'sonner';

export function LivestockPage() {
  const [cows, setCows] = useState<Cow[]>([]);
  const [isLoading, setIsLoading] = useState(false);
  const [statusFilter, setStatusFilter] = useState<CowStatus | 'ALL'>('ALL');
  const [searchQuery, setSearchQuery] = useState('');
  const [isDialogOpen, setIsDialogOpen] = useState(false);
  const [formData, setFormData] = useState({
    tagId: '',
    breed: '',
    dateOfBirth: '',
    acquisitionDate: '',
    status: 'ACTIVE' as CowStatus,
  });
  const navigate = useNavigate();

  const fetchCows = async () => {
    setIsLoading(true);
    try {
      const params = statusFilter !== 'ALL' ? { status: statusFilter } : {};
      const response = await cowService.getCows(params);
      setCows(response.content);
    } catch (error) {
      toast.error(handleApiError(error));
    } finally {
      setIsLoading(false);
    }
  };

  useState(() => {
    fetchCows();
  });

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      await cowService.createCow(formData);
      toast.success('Cow registered successfully');
      setIsDialogOpen(false);
      setFormData({
        tagId: '',
        breed: '',
        dateOfBirth: '',
        acquisitionDate: '',
        status: 'ACTIVE',
      });
      fetchCows();
    } catch (error) {
      toast.error(handleApiError(error));
    }
  };

  const handleStatusUpdate = async (cowId: number, newStatus: CowStatus, cow: Cow) => {
    try {
      await cowService.updateCow(cowId, {
        tagId: cow.tagId,
        breed: cow.breed,
        dateOfBirth: cow.dateOfBirth,
        acquisitionDate: cow.acquisitionDate,
        status: newStatus,
      });
      toast.success(`Cow status updated to ${newStatus}`);
      fetchCows();
    } catch (error) {
      toast.error(handleApiError(error));
    }
  };

  const filteredCows = cows.filter((cow) =>
    cow.tagId.toLowerCase().includes(searchQuery.toLowerCase()) ||
    cow.breed.toLowerCase().includes(searchQuery.toLowerCase())
  );

  const getStatusBadge = (status: CowStatus) => {
    const variants: Record<CowStatus, string> = {
      ACTIVE: 'bg-green-100 text-green-800',
      SOLD: 'bg-blue-100 text-blue-800',
      DECEASED: 'bg-gray-100 text-gray-800',
    };
    return <Badge className={variants[status]}>{status}</Badge>;
  };

  return (
    <div className="min-h-screen" style={{ backgroundColor: '#f8faf9' }}>
      <div className="p-6 space-y-6">
        <div className="flex items-center justify-between">
          <div>
            <h1 className="text-2xl font-semibold">Livestock Management</h1>
            <p className="text-muted-foreground mt-1">Manage your dairy cows</p>
          </div>
          <Dialog open={isDialogOpen} onOpenChange={setIsDialogOpen}>
            <DialogTrigger asChild>
              <Button>
                <Plus className="w-4 h-4 mr-2" />
                Register New Cow
              </Button>
            </DialogTrigger>
            <DialogContent>
              <DialogHeader>
                <DialogTitle>Register New Cow</DialogTitle>
                <DialogDescription>Add a new cow to your farm inventory</DialogDescription>
              </DialogHeader>
              <form onSubmit={handleSubmit} className="space-y-4">
                <div className="space-y-2">
                  <Label htmlFor="tagId">Tag ID *</Label>
                  <Input
                    id="tagId"
                    value={formData.tagId}
                    onChange={(e) => setFormData({ ...formData, tagId: e.target.value })}
                    placeholder="COW-001"
                    required
                  />
                </div>
                <div className="space-y-2">
                  <Label htmlFor="breed">Breed *</Label>
                  <Input
                    id="breed"
                    value={formData.breed}
                    onChange={(e) => setFormData({ ...formData, breed: e.target.value })}
                    placeholder="Holstein"
                    required
                  />
                </div>
                <div className="space-y-2">
                  <Label htmlFor="dateOfBirth">Date of Birth *</Label>
                  <Input
                    id="dateOfBirth"
                    type="date"
                    value={formData.dateOfBirth}
                    onChange={(e) => setFormData({ ...formData, dateOfBirth: e.target.value })}
                    required
                  />
                </div>
                <div className="space-y-2">
                  <Label htmlFor="acquisitionDate">Acquisition Date *</Label>
                  <Input
                    id="acquisitionDate"
                    type="date"
                    value={formData.acquisitionDate}
                    onChange={(e) => setFormData({ ...formData, acquisitionDate: e.target.value })}
                    required
                  />
                </div>
                <div className="space-y-2">
                  <Label htmlFor="status">Status *</Label>
                  <Select
                    value={formData.status}
                    onValueChange={(value: CowStatus) => setFormData({ ...formData, status: value })}
                  >
                    <SelectTrigger>
                      <SelectValue />
                    </SelectTrigger>
                    <SelectContent>
                      <SelectItem value="ACTIVE">Active</SelectItem>
                      <SelectItem value="SOLD">Sold</SelectItem>
                      <SelectItem value="DECEASED">Deceased</SelectItem>
                    </SelectContent>
                  </Select>
                </div>
                <div className="flex gap-2">
                  <Button type="submit" className="flex-1">Register Cow</Button>
                  <Button type="button" variant="outline" onClick={() => setIsDialogOpen(false)}>
                    Cancel
                  </Button>
                </div>
              </form>
            </DialogContent>
          </Dialog>
        </div>

        <Card>
          <CardHeader>
            <CardTitle>Cows</CardTitle>
            <CardDescription>View and manage your livestock</CardDescription>
            <div className="flex gap-4 mt-4">
              <div className="flex-1 relative">
                <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-muted-foreground w-4 h-4" />
                <Input
                  placeholder="Search by tag ID or breed..."
                  value={searchQuery}
                  onChange={(e) => setSearchQuery(e.target.value)}
                  className="pl-10"
                />
              </div>
              <Select value={statusFilter} onValueChange={(value: any) => setStatusFilter(value)}>
                <SelectTrigger className="w-40">
                  <SelectValue />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="ALL">All Status</SelectItem>
                  <SelectItem value="ACTIVE">Active</SelectItem>
                  <SelectItem value="SOLD">Sold</SelectItem>
                  <SelectItem value="DECEASED">Deceased</SelectItem>
                </SelectContent>
              </Select>
            </div>
          </CardHeader>
          <CardContent>
            {isLoading ? (
              <div className="text-center py-8">Loading...</div>
            ) : filteredCows.length === 0 ? (
              <div className="text-center py-8 text-muted-foreground">
                No cows found. Register your first cow to get started.
              </div>
            ) : (
              <Table>
                <TableHeader>
                  <TableRow>
                    <TableHead>Tag ID</TableHead>
                    <TableHead>Breed</TableHead>
                    <TableHead>Date of Birth</TableHead>
                    <TableHead>Acquisition Date</TableHead>
                    <TableHead>Status</TableHead>
                    <TableHead>Actions</TableHead>
                  </TableRow>
                </TableHeader>
                <TableBody>
                  {filteredCows.map((cow) => (
                    <TableRow key={cow.id} className="hover:bg-muted/50">
                      <TableCell className="font-medium">{cow.tagId}</TableCell>
                      <TableCell>{cow.breed}</TableCell>
                      <TableCell>{formatDate(cow.dateOfBirth)}</TableCell>
                      <TableCell>{formatDate(cow.acquisitionDate)}</TableCell>
                      <TableCell>{getStatusBadge(cow.status)}</TableCell>
                      <TableCell>
                        <div className="flex items-center gap-2">
                          <Button 
                            variant="ghost" 
                            size="sm"
                            onClick={() => navigate(`/livestock/${cow.id}`)}
                          >
                            View Details
                          </Button>
                          <DropdownMenu>
                            <DropdownMenuTrigger asChild>
                              <Button variant="ghost" size="icon" className="h-8 w-8">
                                <MoreVertical className="h-4 w-4" />
                              </Button>
                            </DropdownMenuTrigger>
                            <DropdownMenuContent align="end">
                              <DropdownMenuLabel>Update Status</DropdownMenuLabel>
                              <DropdownMenuSeparator />
                              <DropdownMenuItem
                                onClick={(e) => {
                                  e.stopPropagation();
                                  handleStatusUpdate(cow.id, 'ACTIVE', cow);
                                }}
                                disabled={cow.status === 'ACTIVE'}
                              >
                                Mark as Active
                              </DropdownMenuItem>
                              <DropdownMenuItem
                                onClick={(e) => {
                                  e.stopPropagation();
                                  handleStatusUpdate(cow.id, 'SOLD', cow);
                                }}
                                disabled={cow.status === 'SOLD'}
                              >
                                Mark as Sold
                              </DropdownMenuItem>
                              <DropdownMenuItem
                                onClick={(e) => {
                                  e.stopPropagation();
                                  handleStatusUpdate(cow.id, 'DECEASED', cow);
                                }}
                                disabled={cow.status === 'DECEASED'}
                              >
                                Mark as Deceased
                              </DropdownMenuItem>
                            </DropdownMenuContent>
                          </DropdownMenu>
                        </div>
                      </TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            )}
          </CardContent>
        </Card>
      </div>
    </div>
  );
}
