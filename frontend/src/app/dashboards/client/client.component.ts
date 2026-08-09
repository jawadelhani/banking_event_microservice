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

  // ===== PAGINATION =====
  currentPage = 1;
  pageSize = 5;

  get paginatedAccounts(): Account[] {
    const startIndex = (this.currentPage - 1) * this.pageSize;
    return this.accounts.slice(startIndex, startIndex + this.pageSize);
  }

  get paginatedAlerts(): AgencyAlert[] {
    const startIndex = (this.currentPage - 1) * this.pageSize;
    return this.alerts.slice(startIndex, startIndex + this.pageSize);
  }

  get paginatedNotifications(): Notification[] {
    const startIndex = (this.currentPage - 1) * this.pageSize;
    return this.notifications.slice(startIndex, startIndex + this.pageSize);
  }

  get paginatedTransactions(): Transaction[] {
    const startIndex = (this.currentPage - 1) * this.pageSize;
    return this.transactions.slice(startIndex, startIndex + this.pageSize);
  }

  getTotalPages(arrayLength: number): number {
    return Math.ceil(arrayLength / this.pageSize) || 1;
  }

  nextPage(arrayLength: number): void {
    if (this.currentPage < this.getTotalPages(arrayLength)) {
      this.currentPage++;
    }
  }

  prevPage(): void {
    if (this.currentPage > 1) {
      this.currentPage--;
    }
  }
}











