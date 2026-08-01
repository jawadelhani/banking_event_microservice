import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import {
  Account,
  AgencyAlert,
  Client,
  Notification,
  Transaction
} from '../models/banking.models';

@Injectable({
  providedIn: 'root'
})  
export class ApiService {
  constructor(private http: HttpClient) {}

  // ===== CLIENTS =====
  getAllClients(): Observable<Client[]> {
    return this.http.get<Client[]>('/account-service/clients');
  }

  getMyProfile(): Observable<Client> {
    return this.http.get<Client>('/account-service/clients/me');
  }

  // ===== ACCOUNTS - CRUD =====
  getAllAccounts(): Observable<Account[]> {
    return this.http.get<Account[]>('/account-service/accounts');
  }
  
  getMyAccounts(): Observable<Account[]> {
  return this.http.get<Account[]>('/account-service/accounts/me');
}

  getAccount(id: string): Observable<Account> {
    return this.http.get<Account>(`/account-service/accounts/${id}`);
  }

  createAccount(accountData: { clientId: string; accountNumber: string; accountType: string; balance?: number }): Observable<Account> {
    return this.http.post<Account>('/account-service/accounts', accountData);
  }

  updateAccount(id: string, accountData: Partial<Account>): Observable<Account> {
    return this.http.put<Account>(`/account-service/accounts/${id}`, accountData);
  }

  deleteAccount(id: string): Observable<void> {
    return this.http.delete<void>(`/account-service/accounts/${id}`);
  }

  // ===== ALERTS =====
  getAllAlerts(): Observable<AgencyAlert[]> {
    return this.http.get<AgencyAlert[]>('/agency-service/alerts');
  }

  getMyAlerts(): Observable<AgencyAlert[]> {
    return this.http.get<AgencyAlert[]>('/agency-service/alerts/me');
  }

  // ===== NOTIFICATIONS =====
  getAllNotifications(): Observable<Notification[]> {
    return this.http.get<Notification[]>('/notification-service/notifications');
  }

  getMyNotifications(): Observable<Notification[]> {
    return this.http.get<Notification[]>('/notification-service/notifications/me');
  }

  // ===== TRANSACTIONS =====
  getAllTransactions(): Observable<Transaction[]> {
    return this.http.get<Transaction[]>('/agency-service/transactions');
  }

  getMyTransactions(): Observable<Transaction[]> {
    return this.http.get<Transaction[]>('/agency-service/transactions/me');
  }

  
}
