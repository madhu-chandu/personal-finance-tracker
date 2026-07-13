import { Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, tap } from 'rxjs';
import { environment } from '../../../environments/environment';
import { AuthResponse } from '../models/models';

const TOKEN_KEY = 'finance_tracker_token';
const USER_KEY = 'finance_tracker_user';

@Injectable({ providedIn: 'root' })
export class AuthService {
  currentUsername = signal<string | null>(localStorage.getItem(USER_KEY));

  constructor(private http: HttpClient, private router: Router) {}

  signup(username: string, email: string, password: string): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${environment.apiUrl}/auth/signup`, { username, email, password })
      .pipe(tap(res => this.setSession(res)));
  }

  login(username: string, password: string): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${environment.apiUrl}/auth/login`, { username, password })
      .pipe(tap(res => this.setSession(res)));
  }

  logout(): void {
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(USER_KEY);
    this.currentUsername.set(null);
    this.router.navigate(['/login']);
  }

  private setSession(res: AuthResponse): void {
    localStorage.setItem(TOKEN_KEY, res.token);
    localStorage.setItem(USER_KEY, res.username);
    this.currentUsername.set(res.username);
  }

  getToken(): string | null {
    return localStorage.getItem(TOKEN_KEY);
  }

  isLoggedIn(): boolean {
    return !!this.getToken();
  }
}
