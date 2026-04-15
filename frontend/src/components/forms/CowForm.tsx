import { useForm } from 'react-hook-form';
import { Cow } from '../../types/cow.types';
import { cowService } from '../../services/cowService';
import { handleApiError } from '../../utils/errorHandler';
import { Button } from '../ui/button';
import { Input } from '../ui/input';
import { Label } from '../ui/label';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '../ui/select';
import { toast } from 'sonner';

interface CowFormData {
  tagId: string;
  breed: string;
  dateOfBirth: string;
  acquisitionDate: string;
  status: 'ACTIVE' | 'SOLD' | 'DECEASED';
}

interface CowFormProps {
  initialData?: Cow;
  onSuccess: () => void;
  onCancel: () => void;
}

export function CowForm({ initialData, onSuccess, onCancel }: CowFormProps) {
  const {
    register,
    handleSubmit,
    setValue,
    watch,
    formState: { errors, isSubmitting },
  } = useForm<CowFormData>({
    defaultValues: initialData
      ? {
          tagId: initialData.tagId,
          breed: initialData.breed,
          dateOfBirth: initialData.dateOfBirth,
          acquisitionDate: initialData.acquisitionDate,
          status: initialData.status,
        }
      : {
          status: 'ACTIVE',
        },
  });

  const status = watch('status');

  const onSubmit = async (data: CowFormData) => {
    try {
      if (initialData) {
        await cowService.updateCow(initialData.id, data);
      } else {
        await cowService.createCow(data);
      }
      onSuccess();
    } catch (error) {
      toast.error(handleApiError(error));
    }
  };

  return (
    <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
      <div className="space-y-2">
        <Label htmlFor="tagId">Tag ID *</Label>
        <Input
          id="tagId"
          {...register('tagId', { required: 'Tag ID is required' })}
          placeholder="e.g., COW001"
        />
        {errors.tagId && (
          <p className="text-sm text-red-500">{errors.tagId.message}</p>
        )}
      </div>

      <div className="space-y-2">
        <Label htmlFor="breed">Breed *</Label>
        <Input
          id="breed"
          {...register('breed', { required: 'Breed is required' })}
          placeholder="e.g., Holstein"
        />
        {errors.breed && (
          <p className="text-sm text-red-500">{errors.breed.message}</p>
        )}
      </div>

      <div className="space-y-2">
        <Label htmlFor="dateOfBirth">Date of Birth *</Label>
        <Input
          id="dateOfBirth"
          type="date"
          {...register('dateOfBirth', {
            required: 'Date of birth is required',
            validate: (value) => {
              const date = new Date(value);
              const today = new Date();
              if (date > today) {
                return 'Date of birth cannot be in the future';
              }
              return true;
            },
          })}
        />
        {errors.dateOfBirth && (
          <p className="text-sm text-red-500">{errors.dateOfBirth.message}</p>
        )}
      </div>

      <div className="space-y-2">
        <Label htmlFor="acquisitionDate">Acquisition Date *</Label>
        <Input
          id="acquisitionDate"
          type="date"
          {...register('acquisitionDate', {
            required: 'Acquisition date is required',
            validate: (value) => {
              const date = new Date(value);
              const today = new Date();
              if (date > today) {
                return 'Acquisition date cannot be in the future';
              }
              return true;
            },
          })}
        />
        {errors.acquisitionDate && (
          <p className="text-sm text-red-500">{errors.acquisitionDate.message}</p>
        )}
      </div>

      <div className="space-y-2">
        <Label htmlFor="status">Status *</Label>
        <Select
          value={status}
          onValueChange={(value) => setValue('status', value as any)}
        >
          <SelectTrigger>
            <SelectValue placeholder="Select status" />
          </SelectTrigger>
          <SelectContent>
            <SelectItem value="ACTIVE">Active</SelectItem>
            <SelectItem value="SOLD">Sold</SelectItem>
            <SelectItem value="DECEASED">Deceased</SelectItem>
          </SelectContent>
        </Select>
      </div>

      <div className="flex justify-end gap-2 pt-4">
        <Button type="button" variant="outline" onClick={onCancel}>
          Cancel
        </Button>
        <Button type="submit" disabled={isSubmitting}>
          {isSubmitting ? 'Saving...' : initialData ? 'Update' : 'Create'}
        </Button>
      </div>
    </form>
  );
}
