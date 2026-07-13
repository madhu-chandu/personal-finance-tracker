import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Transaction } from '../models/models';

@Injectable({ providedIn: 'root' })
export class TransactionService {
  private baseUrl = `${environment.apiUrl}/transactions`;

  constructor(private http: HttpClient) {}

  getAll(): Observable<Transaction[]> {
    return this.http.get<Transaction[]>(this.baseUrl);
  }

  create(tx: Transaction): Observable<Transaction> {
    return this.http.post<Transaction>(this.baseUrl, tx);
  }

  update(id: number, tx: Transaction): Observable<Transaction> {
    return this.http.put<Transaction>(`${this.baseUrl}/${id}`, tx);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}
