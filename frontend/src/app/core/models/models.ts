export type TransactionType = 'INCOME' | 'EXPENSE';

export interface Category {
  id: number;
  name: string;
  type: TransactionType;
}

export interface Transaction {
  id?: number;
  categoryId: number;
  categoryName?: string;
  amount: number;
  type: TransactionType;
  description: string;
  date: string; // yyyy-MM-dd
}

export interface Budget {
  id?: number;
  categoryId: number;
  categoryName?: string;
  monthlyLimit: number;
  month: number;
  year: number;
  spent?: number;
  overBudget?: boolean;
}

export interface DashboardSummary {
  totalIncome: number;
  totalExpense: number;
  balance: number;
  expenseByCategory: { [key: string]: number };
  incomeByCategory: { [key: string]: number };
  budgetStatus: Budget[];
}

export interface AuthResponse {
  token: string;
  username: string;
  email: string;
}
