import { useForm } from 'react-hook-form';
import { cowService } from '../../services/cowService';
import { handleApiError } from '../../utils/errorHandler';
import { Button } from '../ui/button';
import { Input } from '../ui/input';
import { Label } from '../ui/label';
import { Textarea } from '../ui/textarea';
import { toast } from 'sonner';

interface BreedingRecordFormData {
  breedingDate: string;
  bullId: string;
  expectedCalvingDate: string;
  notes?: string;
}

interface BreedingRecordFormProps {
  cowId: number;
  onSuccess: () => void;
  onCancel: () => void;
}

export function BreedingRecordForm({ cowId, onSuccess, onCancel }: BreedingRecordFormProps) {
  const {
    register,
    handleSubmit,
    watch,
    formState: { errors, isSubmitting },
  } = useForm<BreedingRecordFormData>();

  const breedingDate = watch('breedingDate');

  const onSubmit = async (data: BreedingRecordFormData) => {
    try {
      await cowService.addBreedingRecord(cowId, data);
      onSuccess();
    } catch (error) {
      toast.error(handleApiError(error));
    }
  };

  return (
    <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
      <div className="space-y-2">
        <Label htmlFor="breedingDate">Breeding Date *</Label>
        <Input
          id="breedingDate"
          type="date"
          {...register('breedingDate', {
            required: 'Breeding date is required',
            validate: (value) => {
              const date = new Date(value);
              const today = new Date();
              if (date > today) {
                return 'Breeding date cannot be in the future';
              }
              return true;
            },
          })}
        />
        {errors.breedingDate && (
          <p className="text-sm text-red-500">{errors.breedingDate.message}</p>
        )}
      </div>

      <div className="space-y-2">
        <Label htmlFor="bullId">Bull ID *</Label>
        <Input
          id="bullId"
          {...register('bullId', { required: 'Bull ID is required' })}
          placeholder="e.g., BULL001"
        />
        {errors.bullId && (
          <p className="text-sm text-red-500">{errors.bullId.message}</p>
        )}
      </div>

      <div className="space-y-2">
        <Label htmlFor="expectedCalvingDate">Expected Calving Date *</Label>
        <Input
          id="expectedCalvingDate"
          type="date"
          {...register('expectedCalvingDate', {
            required: 'Expected calving date is required',
            validate: (value) => {
              if (!breedingDate) return true;
              const calvingDate = new Date(value);
              const breeding = new Date(breedingDate);
              if (calvingDate <= breeding) {
                return 'Expected calving date must be after breeding date';
              }
              return true;
            },
          })}
        />
        {errors.expectedCalvingDate && (
          <p className="text-sm text-red-500">{errors.expectedCalvingDate.message}</p>
        )}
      </div>

      <div className="space-y-2">
        <Label htmlFor="notes">Notes</Label>
        <Textarea
          id="notes"
          {...register('notes')}
          placeholder="Additional notes about the breeding..."
          rows={3}
        />
      </div>

      <div className="flex justify-end gap-2 pt-4">
        <Button type="button" variant="outline" onClick={onCancel}>
          Cancel
        </Button>
        <Button type="submit" disabled={isSubmitting}>
          {isSubmitting ? 'Adding...' : 'Add Record'}
        </Button>
      </div>
    </form>
  );
}
