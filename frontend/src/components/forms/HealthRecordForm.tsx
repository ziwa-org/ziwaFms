import { useForm } from 'react-hook-form';
import { useState } from 'react';
import { HealthRecord, HealthRecordType } from '../../types/health.types';
import { Cow } from '../../types/cow.types';
import { healthService } from '../../services/healthService';
import { parseApiError } from '../../utils/errorHandler';
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
import { AlertCircle } from 'lucide-react';

interface HealthRecordFormData {
  cowId: number;
  date: string;
  recordType: HealthRecordType;
  description: string;
  veterinarianName?: string;
  medication?: string;
  withdrawalPeriodDays?: number;
  cost?: number;
}

interface HealthRecordFormProps {
  initialData?: HealthRecord;
  cows: Cow[];
  onSuccess: () => void;
  onCancel: () => void;
}

export function HealthRecordForm({
  initialData,
  cows,
  onSuccess,
  onCancel,
}: HealthRecordFormProps) {
  const [apiErrors, setApiErrors] = useState<Record<string, string>>({});
  const [generalError, setGeneralError] = useState<string>('');
  
  const {
    register,
    handleSubmit,
    setValue,
    watch,
    formState: { errors, isSubmitting },
  } = useForm<HealthRecordFormData>({
    defaultValues: initialData
      ? {
          cowId: initialData.cowId,
          date: initialData.date,
          recordType: initialData.recordType,
          description: initialData.description,
          veterinarianName: initialData.veterinarianName,
          medication: initialData.medication,
          withdrawalPeriodDays: initialData.withdrawalPeriodDays,
          cost: initialData.cost,
        }
      : {
          date: new Date().toISOString().split('T')[0], // Default to today
        },
  });

  const cowId = watch('cowId');
  const recordType = watch('recordType');

  const onSubmit = async (data: HealthRecordFormData) => {
    // Clear previous errors
    setApiErrors({});
    setGeneralError('');
    
    // Validate that a cow is selected
    if (!data.cowId) {
      toast.error('Please select a cow');
      return;
    }

    // Validate that a record type is selected
    if (!data.recordType) {
      toast.error('Please select a record type');
      return;
    }

    try {
      if (initialData) {
        await healthService.updateHealthRecord(initialData.id, data);
        toast.success('Health record updated successfully');
      } else {
        await healthService.createHealthRecord(data);
        toast.success('Health record created successfully');
      }
      onSuccess();
    } catch (error) {
      const parsedError = parseApiError(error);
      
      // Display general error message
      toast.error(parsedError.message);
      setGeneralError(parsedError.message);
      
      // Set field-level errors if present
      if (parsedError.fieldErrors) {
        setApiErrors(parsedError.fieldErrors);
      }
      
      // Log error for debugging
      console.error('Health record submission error:', error);
    }
  };

  return (
    <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
      {/* General error alert */}
      {generalError && (
        <div className="p-4 bg-red-50 border border-red-200 rounded-md flex items-start gap-2">
          <AlertCircle className="h-5 w-5 text-red-600 flex-shrink-0 mt-0.5" />
          <div>
            <p className="text-sm font-medium text-red-800">Error</p>
            <p className="text-sm text-red-700">{generalError}</p>
          </div>
        </div>
      )}
      
      {cows.length === 0 && (
        <div className="p-4 bg-yellow-50 border border-yellow-200 rounded-md">
          <p className="text-sm text-yellow-800">
            No cows available. Please register cows in the Livestock page first.
          </p>
        </div>
      )}

      <div className="space-y-2">
        <Label htmlFor="cowId">Cow *</Label>
        <Select
          value={cowId?.toString()}
          onValueChange={(value) => setValue('cowId', parseInt(value))}
          disabled={cows.length === 0}
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
          <p className="text-sm text-red-500">{errors.cowId.message}</p>
        )}
        {apiErrors.cowId && (
          <p className="text-sm text-red-500">{apiErrors.cowId}</p>
        )}
      </div>

      <div className="space-y-2">
        <Label htmlFor="date">Record Date *</Label>
        <Input
          id="date"
          type="date"
          {...register('date', {
            required: 'Record date is required',
            validate: (value) => {
              const date = new Date(value);
              const today = new Date();
              if (date > today) {
                return 'Record date cannot be in the future';
              }
              return true;
            },
          })}
        />
        {errors.date && (
          <p className="text-sm text-red-500">{errors.date.message}</p>
        )}
        {apiErrors.date && (
          <p className="text-sm text-red-500">{apiErrors.date}</p>
        )}
      </div>

      <div className="space-y-2">
        <Label htmlFor="recordType">Record Type *</Label>
        <Select
          value={recordType}
          onValueChange={(value) => setValue('recordType', value as HealthRecordType)}
        >
          <SelectTrigger>
            <SelectValue placeholder="Select record type" />
          </SelectTrigger>
          <SelectContent>
            <SelectItem value="VACCINATION">Vaccination</SelectItem>
            <SelectItem value="TREATMENT">Treatment</SelectItem>
            <SelectItem value="CHECKUP">Checkup</SelectItem>
          </SelectContent>
        </Select>
        {errors.recordType && (
          <p className="text-sm text-red-500">Record type is required</p>
        )}
        {apiErrors.recordType && (
          <p className="text-sm text-red-500">{apiErrors.recordType}</p>
        )}
      </div>

      <div className="space-y-2">
        <Label htmlFor="description">Description *</Label>
        <Textarea
          id="description"
          {...register('description', { required: 'Description is required' })}
          placeholder="Describe the health event..."
          rows={3}
        />
        {errors.description && (
          <p className="text-sm text-red-500">{errors.description.message}</p>
        )}
        {apiErrors.description && (
          <p className="text-sm text-red-500">{apiErrors.description}</p>
        )}
      </div>

      <div className="space-y-2">
        <Label htmlFor="veterinarianName">Veterinarian</Label>
        <Input
          id="veterinarianName"
          {...register('veterinarianName')}
          placeholder="Dr. Smith"
        />
        {apiErrors.veterinarianName && (
          <p className="text-sm text-red-500">{apiErrors.veterinarianName}</p>
        )}
      </div>

      <div className="space-y-2">
        <Label htmlFor="medication">Medication</Label>
        <Input
          id="medication"
          {...register('medication')}
          placeholder="Medication name"
        />
        {apiErrors.medication && (
          <p className="text-sm text-red-500">{apiErrors.medication}</p>
        )}
      </div>

      <div className="grid grid-cols-2 gap-4">
        <div className="space-y-2">
          <Label htmlFor="withdrawalPeriodDays">Withdrawal Period (days)</Label>
          <Input
            id="withdrawalPeriodDays"
            type="number"
            {...register('withdrawalPeriodDays', {
              min: { value: 0, message: 'Must be non-negative' },
              valueAsNumber: true,
            })}
            placeholder="0"
          />
          {errors.withdrawalPeriodDays && (
            <p className="text-sm text-red-500">{errors.withdrawalPeriodDays.message}</p>
          )}
          {apiErrors.withdrawalPeriodDays && (
            <p className="text-sm text-red-500">{apiErrors.withdrawalPeriodDays}</p>
          )}
        </div>

        <div className="space-y-2">
          <Label htmlFor="cost">Cost ($)</Label>
          <Input
            id="cost"
            type="number"
            step="0.01"
            {...register('cost', {
              min: { value: 0, message: 'Must be non-negative' },
              valueAsNumber: true,
            })}
            placeholder="0.00"
          />
          {errors.cost && (
            <p className="text-sm text-red-500">{errors.cost.message}</p>
          )}
          {apiErrors.cost && (
            <p className="text-sm text-red-500">{apiErrors.cost}</p>
          )}
        </div>
      </div>

      <div className="flex justify-end gap-2 pt-4">
        <Button type="button" variant="outline" onClick={onCancel}>
          Cancel
        </Button>
        <Button type="submit" disabled={isSubmitting || cows.length === 0}>
          {isSubmitting ? 'Saving...' : initialData ? 'Update' : 'Create'}
        </Button>
      </div>
    </form>
  );
}
