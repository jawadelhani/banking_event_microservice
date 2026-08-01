import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { forkJoin } from 'rxjs';
import { AuthService } from '../../services/auth.service';
import { ApiService } from '../../services/api.service';
import {
  Account,
  AgencyAlert,
  Client,
  Notification,
  Transaction
} from '../../models/banking.models';

@Component({
  selector: 'app-client',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './client.component.html',
  styleUrl: './client.component.css'
})
export class ClientComponent implements OnInit {
  loading = false;
  errorMessage = '';

  profile: Client | null = null;
  accounts: Account[] = [];
  alerts: AgencyAlert[] = [];
  notifications: Notification[] = [];
  transactions: Transaction[] = [];

  constructor(
    public authService: AuthService,
    private apiService: ApiService
  ) {}

  ngOnInit(): void {
    this.loadData();
  }

  loadData(): void {
    this.loading = true;
    this.errorMessage = '';

    Promise.all([
      this.apiService.getMyProfile().toPromise(),
      this.apiService.getMyAccounts().toPromise(),
      this.apiService.getMyAlerts().toPromise(),
      this.apiService.getMyNotifications().toPromise(),
      this.apiService.getMyTransactions().toPromise()
    ])
      .then(([profile, accounts, alerts, notifications, transactions]) => {
        this.profile = profile ?? null;
        this.accounts = accounts ?? [];
        this.alerts = alerts ?? [];
        this.notifications = notifications ?? [];
        this.transactions = transactions ?? [];
      })
      .catch(() => {
        this.errorMessage = 'Failed to load your data. Please try again.';
      })
      .finally(() => {
        this.loading = false;
      });
  }

  logout(): void {
    this.authService.logout();
  }

  totalBalance(): number {
    return this.accounts.reduce((sum, account) => sum + (account.balance ?? 0), 0);
  }

  formatDate(value?: string): string {
    return value ? new Date(value).toLocaleString() : '—';
  }

  formatAmount(value?: number): string {
    return value != null
      ? new Intl.NumberFormat('fr-MA', { style: 'currency', currency: 'MAD' }).format(value)
      : '—';
  }
}











