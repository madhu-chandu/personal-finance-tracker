import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { TransactionService } from '../../core/services/transaction.service';
import { CategoryService } from '../../core/services/category.service';
import { Category, Transaction, TransactionType } from '../../core/models/models';

@Component({
  selector: 'app-transactions',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './transactions.component.html',
  styleUrls: ['./transactions.component.css']
})
export class TransactionsComponent implements OnInit {
  transactions: Transaction[] = [];
  categories: Category[] = [];
  loading = true;
  error = '';
  showForm = false;
  editingId: number | null = null;

  form!: FormGroup;

  constructor(
    private fb: FormBuilder,
    private transactionService: TransactionService,
    private categoryService: CategoryService
  ) {}

 ngOnInit(): void {

  this.form = this.fb.group({
    type: ['EXPENSE' as TransactionType, Validators.required],
    categoryId: [null, Validators.required],
    amount: [null, [Validators.required, Validators.min(0.01)]],
    date: [this.today(), Validators.required],
    description: ['']
  });

  this.categoryService.getAll().subscribe(cats => this.categories = cats);
  this.loadTransactions();
}

  get filteredCategories(): Category[] {
    const type = this.form.value.type;
    return this.categories.filter(c => c.type === type);
  }

  loadTransactions(): void {
    this.loading = true;
    this.transactionService.getAll().subscribe({
      next: (data) => { this.transactions = data; this.loading = false; },
      error: () => { this.error = 'Unable to load transactions.'; this.loading = false; }
    });
  }

  openAddForm(): void {
    this.editingId = null;
    this.form.reset({ type: 'EXPENSE', categoryId: null, amount: null, date: this.today(), description: '' });
    this.showForm = true;
  }

  editTransaction(tx: Transaction): void {
    this.editingId = tx.id!;
    this.form.setValue({
      type: tx.type,
      categoryId: tx.categoryId,
      amount: tx.amount,
      date: tx.date,
      description: tx.description || ''
    });
    this.showForm = true;
  }

  cancelForm(): void {
    this.showForm = false;
    this.editingId = null;
  }

  submit(): void {
    if (this.form.invalid) return;
    const payload: Transaction = this.form.value;

    const request$ = this.editingId
      ? this.transactionService.update(this.editingId, payload)
      : this.transactionService.create(payload);

    request$.subscribe({
      next: () => {
        this.showForm = false;
        this.editingId = null;
        this.loadTransactions();
      },
      error: () => { this.error = 'Failed to save transaction.'; }
    });
  }

  deleteTransaction(id: number): void {
    if (!confirm('Delete this transaction?')) return;
    this.transactionService.delete(id).subscribe({
      next: () => this.loadTransactions(),
      error: () => { this.error = 'Failed to delete transaction.'; }
    });
  }

  private today(): string {
    return new Date().toISOString().slice(0, 10);
  }
}
