import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { Router } from '@angular/router';
import { LoginRequest, LoginResponse, RegisterRequest } from '../models/banking.models';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private tokenKey = 'jwt_token';
  private roleKey = 'user_role';

  private authStatusSubject = new BehaviorSubject<boolean>(this.hasToken());
  public authStatus$ = this.authStatusSubject.asObservable();

  constructor(private http: HttpClient, private router: Router) {
    // Ensure role is cleared if token doesn't exist
    if (!this.hasToken()) {
      localStorage.removeItem(this.roleKey);
    }
  }

  login(credentials: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>('/account-service/auth/login', credentials).pipe(
      tap(res => {
        if (res?.accessToken) {
          this.setToken(res.accessToken);
          this.setRole(this.extractRole(res.accessToken));
          this.authStatusSubject.next(true);
        }
      })
    );
  }

  register(userData: RegisterRequest): Observable<unknown> {
    return this.http.post('/account-service/auth/register', userData);
  }

  logout(): void {
    localStorage.removeItem(this.tokenKey);
    localStorage.removeItem(this.roleKey);
    this.authStatusSubject.next(false);
    this.router.navigate(['/login']);
  }

  getToken(): string | null {
    return localStorage.getItem(this.tokenKey);
  }

  setToken(token: string): void {
    localStorage.setItem(this.tokenKey, token);
  }

  getRole(): string | null {
    return localStorage.getItem(this.roleKey);
  }

  setRole(role: string): void {
    localStorage.setItem(this.roleKey, role);
  }

  hasToken(): boolean {
    return !!this.getToken();
  }

  isAdmin(): boolean {
    // Only return true if both token exists AND role is ADMIN
    return this.hasToken() && this.getRole() === 'ADMIN';
  }

  redirectAfterLogin(): void {
    this.router.navigate([this.isAdmin() ? '/admin' : '/client']);
  }

  private extractRole(token: string): string {
    const payload = this.decodeToken(token);
    const roles: string[] = payload?.realm_access?.roles ?? [];
    return roles.includes('ADMIN') ? 'ADMIN' : 'CLIENT';
  }

  private decodeToken(token: string): { realm_access?: { roles?: string[] } } | null {
    try {
      return JSON.parse(atob(token.split('.')[1]));
    } catch {
      return null;
    }
  }
}
