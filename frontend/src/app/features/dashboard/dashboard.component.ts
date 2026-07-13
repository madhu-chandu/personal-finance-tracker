import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { NgChartsModule } from 'ng2-charts';
import { ChartConfiguration } from 'chart.js';
import { DashboardService } from '../../core/services/dashboard.service';
import { ExportService } from '../../core/services/export.service';
import { DashboardSummary } from '../../core/models/models';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule, NgChartsModule],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {
  summary: DashboardSummary | null = null;
  loading = true;
  error = '';

  month = new Date().getMonth() + 1;
  year = new Date().getFullYear();

  months = [
    'January','February','March','April','May','June',
    'July','August','September','October','November','December'
  ];

  expenseChartData: ChartConfiguration<'doughnut'>['data'] = { labels: [], datasets: [{ data: [] }] };
  expenseChartOptions: ChartConfiguration<'doughnut'>['options'] = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: { legend: { position: 'bottom' } }
  };

  incomeVsExpenseData: ChartConfiguration<'bar'>['data'] = {
    labels: ['Income', 'Expense'],
    datasets: [{ data: [0, 0], backgroundColor: ['#16a34a', '#dc2626'] }]
  };
  incomeVsExpenseOptions: ChartConfiguration<'bar'>['options'] = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: { legend: { display: false } }
  };

  constructor(private dashboardService: DashboardService, private exportService: ExportService) {}

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading = true;
    this.error = '';
    this.dashboardService.getSummary(this.month, this.year).subscribe({
      next: (data) => {
        this.summary = data;
        this.buildCharts(data);
        this.loading = false;
      },
      error: () => {
        this.error = 'Unable to load dashboard data.';
        this.loading = false;
      }
    });
  }

  buildCharts(data: DashboardSummary): void {
    const labels = Object.keys(data.expenseByCategory);
    const values = Object.values(data.expenseByCategory);
    this.expenseChartData = {
      labels,
      datasets: [{
        data: values,
        backgroundColor: ['#4f46e5','#dc2626','#f59e0b','#10b981','#3b82f6','#8b5cf6','#ec4899','#14b8a6','#f97316','#6366f1']
      }]
    };

    this.incomeVsExpenseData = {
      labels: ['Income', 'Expense'],
      datasets: [{ data: [data.totalIncome, data.totalExpense], backgroundColor: ['#16a34a', '#dc2626'] }]
    };
  }

  downloadCsv(): void {
    this.exportService.downloadCsv(this.month, this.year).subscribe(blob => this.download(blob, `transactions-${this.year}-${this.month}.csv`));
  }

  downloadPdf(): void {
    this.exportService.downloadPdf(this.month, this.year).subscribe(blob => this.download(blob, `report-${this.year}-${this.month}.pdf`));
  }

  private download(blob: Blob, filename: string): void {
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = filename;
    a.click();
    window.URL.revokeObjectURL(url);
  }
}
