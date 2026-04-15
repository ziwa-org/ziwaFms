import { useForm } from 'react-hook-form';
import { useState } from 'react';
import { Transaction } from '../../types/financial.types';
import { financialService } from '../../services/financialService';
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

interface TransactionFormData {
  date: string;
  type: 'INCOME' | 'EXPENSE';
  category: string;
  amount: number;
  description: string;
  referenceId?: string;
}

interface TransactionFormProps {
  initialData?: Transaction;
  onSuccess: () => void;
  onCancel: () => void;
}

const INCOME_CATEGORIES = [
  'MILK_SALES',
  'LIVESTOCK_SALES',
  'GOVERNMENT_SUBSIDY',
  'OTHER_INCOME',
];

const EXPENSE_CATEGORIES = [
  'FEED',
  'VETERINARY',
  'LABOR',
  'EQUIPMENT',
  'UTILITIES',
  'MAINTENANCE',
  'OTHER_EXPENSE',
];

export function TransactionForm({
  initialData,
  onSuccess,
  onCancel,
}: TransactionFormProps) {
  const [apiErrors, setApiErrors] = useState<Record<string, string>>({});
  const [generalError, setGeneralError] = useState<string>('');
  
  const {
    register,
    handleSubmit,
    setValue,
    watch,
    formState: { errors, isSubmitting },
  } = useForm<TransactionFormData>({
    defaultValues: initialData
      ? {
          date: initialData.date,
          type: initialData.type,
          category: initialData.category,
          amount: initialData.amount,
          description: initialData.description,
          referenceId: initialData.referenceId,
        }
      : {
          type: 'INCOME',
          category: 'MILK_SALES', // Default to first income category
        },
  });

  const type = watch('type');
  const category = watch('category');

  // Register type and category fields with validation
  register('type', { required: 'Transaction type is required' });
  register('category', { required: 'Category is required' });

  const onSubmit = async (data: TransactionFormData) => {
    // Clear previous errors
    setApiErrors({});
    setGeneralError('');
    
    // Validate that type and category are selected
    if (!data.type) {
      toast.error('Please select a transaction type');
      return;
    }
    
    if (!data.category) {
      toast.error('Please select a category');
      return;
    }
    
    try {
      if (initialData) {
        await financialService.updateTransaction(initialData.id, data);
        toast.success('Transaction updated successfully');
      } else {
        await financialService.createTransaction(data);
        toast.success('Transaction created successfully');
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
      console.error('Transaction submission error:', error);
    }
  };

  const categories = type === 'INCOME' ? INCOME_CATEGORIES : EXPENSE_CATEGORIES;

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
      
      <div className="space-y-2">
        <Label htmlFor="date">Transaction Date *</Label>
        <Input
          id="date"
          type="date"
          {...register('date', {
            required: 'Transaction date is required',
            validate: (value) => {
              const date = new Date(value);
              const today = new Date();
              if (date > today) {
                return 'Transaction date cannot be in the future';
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
        <Label htmlFor="type">Type *</Label>
        <Select
          value={type}
          onValueChange={(value) => {
            setValue('type', value as any, { shouldValidate: true });
            setValue('category', '', { shouldValidate: true }); // Reset category when type changes
          }}
        >
          <SelectTrigger>
            <SelectValue placeholder="Select type" />
          </SelectTrigger>
          <SelectContent>
            <SelectItem value="INCOME">Income</SelectItem>
            <SelectItem value="EXPENSE">Expense</SelectItem>
          </SelectContent>
        </Select>
        {errors.type && (
          <p className="text-sm text-red-500">{errors.type.message}</p>
        )}
        {apiErrors.type && (
          <p className="text-sm text-red-500">{apiErrors.type}</p>
        )}
      </div>

      <div className="space-y-2">
        <Label htmlFor="category">Category *</Label>
        <Select
          value={category}
          onValueChange={(value) => setValue('category', value, { shouldValidate: true })}
        >
          <SelectTrigger>
            <SelectValue placeholder="Select category" />
          </SelectTrigger>
          <SelectContent>
            {categories.map((cat) => (
              <SelectItem key={cat} value={cat}>
                {cat.replace(/_/g, ' ')}
              </SelectItem>
            ))}
          </SelectContent>
        </Select>
        {errors.category && (
          <p className="text-sm text-red-500">{errors.category.message}</p>
        )}
        {apiErrors.category && (
          <p className="text-sm text-red-500">{apiErrors.category}</p>
        )}
      </div>

      <div className="space-y-2">
        <Label htmlFor="amount">Amount ($) *</Label>
        <Input
          id="amount"
          type="number"
          step="0.01"
          {...register('amount', {
            required: 'Amount is required',
            min: { value: 0.01, message: 'Amount must be greater than 0' },
            valueAsNumber: true,
          })}
        />
        {errors.amount && (
          <p className="text-sm text-red-500">{errors.amount.message}</p>
        )}
        {apiErrors.amount && (
          <p className="text-sm text-red-500">{apiErrors.amount}</p>
        )}
      </div>

      <div className="space-y-2">
        <Label htmlFor="description">Description *</Label>
        <Textarea
          id="description"
          {...register('description', { required: 'Description is required' })}
          placeholder="Describe the transaction..."
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
        <Label htmlFor="referenceId">Reference ID</Label>
        <Input
          id="referenceId"
          {...register('referenceId')}
          placeholder="Invoice number, receipt number, etc."
        />
        {apiErrors.referenceId && (
          <p className="text-sm text-red-500">{apiErrors.referenceId}</p>
        )}
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
