import { useForm } from 'react-hook-form';
import { ProductionRecord } from '../../types/production.types';
import { Cow } from '../../types/cow.types';
import { productionService } from '../../services/productionService';
import { handleApiError } from '../../utils/errorHandler';
import { Button } from '../ui/button';
import { Input } from '../ui/input';
import { Label } from '../ui/label';
import { Textarea } from '../ui/textarea';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '../ui/select';
import { toast } from 'sonner';
import { useEffect } from 'react';

interface ProductionRecordFormData {
  cowId: number;
  date: string;
  morningQuantity: number;
  eveningQuantity: number;
  notes?: string;
}

interface ProductionRecordFormProps {
  initialData?: ProductionRecord;
  cows: Cow[];
  onSuccess: () => void;
  onCancel: () => void;
}

export function ProductionRecordForm({
  initialData,
  cows,
  onSuccess,
  onCancel,
}: ProductionRecordFormProps) {
  const {
    register,
    handleSubmit,
    setValue,
    watch,
    formState: { errors, isSubmitting },
  } = useForm<ProductionRecordFormData>({
    defaultValues: initialData
      ? {
          cowId: initialData.cowId,
          date: initialData.date,
          morningQuantity: initialData.morningQuantity,
          eveningQuantity: initialData.eveningQuantity,
          notes: initialData.notes,
        }
      : undefined,
  });

  const cowId = watch('cowId');

  const onSubmit = async (data: ProductionRecordFormData) => {
    try {
      if (initialData) {
        await productionService.updateProductionRecord(initialData.id, data);
      } else {
        await productionService.createProductionRecord(data);
      }
      onSuccess();
    } catch (error) {
      toast.error(handleApiError(error));
    }
  };

  return (
    <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
      <div className="space-y-2">
        <Label htmlFor="cowId">Cow *</Label>
        <Select
          value={cowId?.toString()}
          onValueChange={(value) => setValue('cowId', parseInt(value))}
        >
          <SelectTrigger>
            <SelectValue placeholder="Select a cow" />
          </SelectTrigger>
          <SelectContent>
            {cows.map((cow) => (
              <SelectItem key={cow.id} value={cow.id.toString()}>
                {cow.tagId} - {cow.breed}
              </SelectItem>
            ))}
          </SelectContent>
        </Select>
        {errors.cowId && (
          <p className="text-sm text-red-500">Cow is required</p>
        )}
      </div>

      <div className="space-y-2">
        <Label htmlFor="date">Production Date *</Label>
        <Input
          id="date"
          type="date"
          {...register('date', {
            required: 'Production date is required',
            validate: (value) => {
              const date = new Date(value);
              const today = new Date();
              if (date > today) {
                return 'Production date cannot be in the future';
              }
              return true;
            },
          })}
        />
        {errors.date && (
          <p className="text-sm text-red-500">{errors.date.message}</p>
        )}
      </div>

      <div className="grid grid-cols-2 gap-4">
        <div className="space-y-2">
          <Label htmlFor="morningQuantity">Morning Quantity (L) *</Label>
          <Input
            id="morningQuantity"
            type="number"
            step="0.1"
            {...register('morningQuantity', {
              required: 'Morning quantity is required',
              min: { value: 0, message: 'Quantity must be non-negative' },
              valueAsNumber: true,
            })}
          />
          {errors.morningQuantity && (
            <p className="text-sm text-red-500">{errors.morningQuantity.message}</p>
          )}
        </div>

        <div className="space-y-2">
          <Label htmlFor="eveningQuantity">Evening Quantity (L) *</Label>
          <Input
            id="eveningQuantity"
            type="number"
            step="0.1"
            {...register('eveningQuantity', {
              required: 'Evening quantity is required',
              min: { value: 0, message: 'Quantity must be non-negative' },
              valueAsNumber: true,
            })}
          />
          {errors.eveningQuantity && (
            <p className="text-sm text-red-500">{errors.eveningQuantity.message}</p>
          )}
        </div>
      </div>

      <div className="space-y-2">
        <Label htmlFor="notes">Notes</Label>
        <Textarea
          id="notes"
          {...register('notes')}
          placeholder="Additional notes about the production..."
          rows={3}
        />
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
