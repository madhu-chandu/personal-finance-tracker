import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { BudgetService } from '../../core/services/budget.service';
import { CategoryService } from '../../core/services/category.service';
import { Budget, Category } from '../../core/models/models';

@Component({
  selector: 'app-budgets',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, FormsModule],
  templateUrl: './budgets.component.html',
  styleUrls: ['./budgets.component.css']
})
export class BudgetsComponent implements OnInit {
  budgets: Budget[] = [];
  categories: Category[] = [];
  loading = true;
  error = '';
  showForm = false;
  editingId: number | null = null;

  month = new Date().getMonth() + 1;
  year = new Date().getFullYear();

  months = [
    'January','February','March','April','May','June',
    'July','August','September','October','November','December'
  ];

  form!: FormGroup;

  constructor(
    private fb: FormBuilder,
    private budgetService: BudgetService,
    private categoryService: CategoryService
  ) {}

  ngOnInit(): void {

    this.form = this.fb.group({
      categoryId: [null, Validators.required],
      monthlyLimit: [null, [Validators.required, Validators.min(0.01)]]
    });

    this.categoryService.getAll().subscribe(cats =>
      this.categories = cats.filter(c => c.type === 'EXPENSE')
    );

    this.load();
  }

  load(): void {
    this.loading = true;
    this.budgetService.getForMonth(this.month, this.year).subscribe({
      next: (data) => { this.budgets = data; this.loading = false; },
      error: () => { this.error = 'Unable to load budgets.'; this.loading = false; }
    });
  }

  openAddForm(): void {
    this.editingId = null;
    this.form.reset({ categoryId: null, monthlyLimit: null });
    this.showForm = true;
  }

  editBudget(b: Budget): void {
    this.editingId = b.id!;
    this.form.setValue({
      categoryId: b.categoryId,
      monthlyLimit: b.monthlyLimit
    });
    this.showForm = true;
  }

  cancelForm(): void {
    this.showForm = false;
    this.editingId = null;
  }

  submit(): void {
    if (this.form.invalid) return;

    const payload: Budget = {
      ...this.form.value,
      month: this.month,
      year: this.year
    };

    const request$ = this.editingId
      ? this.budgetService.update(this.editingId, payload)
      : this.budgetService.create(payload);

    request$.subscribe({
      next: () => {
        this.showForm = false;
        this.editingId = null;
        this.load();
      },
      error: () => {
        this.error = 'Failed to save budget.';
      }
    });
  }

  deleteBudget(id: number): void {
    if (!confirm('Delete this budget?')) return;

    this.budgetService.delete(id).subscribe({
      next: () => this.load(),
      error: () => {
        this.error = 'Failed to delete budget.';
      }
    });
  }

  progressPercent(b: Budget): number {
    if (!b.monthlyLimit) return 0;
    const pct = ((b.spent || 0) / b.monthlyLimit) * 100;
    return pct > 100 ? 100 : pct;
  }
}