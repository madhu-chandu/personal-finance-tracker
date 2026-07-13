import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Budget } from '../models/models';

@Injectable({ providedIn: 'root' })
export class BudgetService {
  private baseUrl = `${environment.apiUrl}/budgets`;

  constructor(private http: HttpClient) {}

  getForMonth(month: number, year: number): Observable<Budget[]> {
    return this.http.get<Budget[]>(this.baseUrl, { params: { month, year } });
  }

  create(budget: Budget): Observable<Budget> {
    return this.http.post<Budget>(this.baseUrl, budget);
  }

  update(id: number, budget: Budget): Observable<Budget> {
    return this.http.put<Budget>(`${this.baseUrl}/${id}`, budget);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}
