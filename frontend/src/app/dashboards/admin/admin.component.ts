import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
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

type AdminTab = 'clients' | 'accounts' | 'alerts' | 'notifications' | 'transactions';

@Component({
  selector: 'app-admin',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './admin.component.html',
  styleUrl: './admin.component.css'
})
export class AdminComponent implements OnInit {
  activeTab: AdminTab = 'clients';
  loading = false;
  errorMessage = '';
  successMessage = '';

  clients: Client[] = [];
  accounts: Account[] = [];
  alerts: AgencyAlert[] = [];
  notifications: Notification[] = [];
  transactions: Transaction[] = [];

  // CRUD Modal states
  showCreateAccountModal = false;
  showUpdateAccountModal = false;
  selectedAccount: Account | null = null;
  createOperating = false;
  updateOperating = false;
  deleteOperating: { [key: string]: boolean } = {};

  // Forms
  createAccountForm = this.fb.group({
    clientId: ['', Validators.required],
    accountNumber: ['', Validators.required],
    accountType: ['CHECKING', Validators.required],
    balance: [0, [Validators.required, Validators.min(0)]]
  });

  updateAccountForm = this.fb.group({
    balance: [0, [Validators.required, Validators.min(0)]],
    status: ['ACTIVE', Validators.required]
  });

  accountTypes = ['CHECKING', 'SAVINGS', 'MONEY_MARKET', 'CERTIFICATE_OF_DEPOSIT'];
  accountStatuses = ['ACTIVE', 'INACTIVE', 'SUSPENDED', 'CLOSED'];

  tabs: { id: AdminTab; label: string; count: number }[] = [
    { id: 'clients', label: 'Clients', count: 0 },
    { id: 'accounts', label: 'Accounts', count: 0 },
    { id: 'alerts', label: 'Alerts', count: 0 },
    { id: 'notifications', label: 'Notifications', count: 0 },
    { id: 'transactions', label: 'Transactions', count: 0 }
  ];

  constructor(
    public authService: AuthService,
    private apiService: ApiService,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.loadAll();
  }

  selectTab(tab: AdminTab): void {
    this.activeTab = tab;
  }

  loadAll(): void {
    this.loading = true;
    this.errorMessage = '';

    forkJoin({
      clients: this.apiService.getAllClients(),
      accounts: this.apiService.getAllAccounts(),
      alerts: this.apiService.getAllAlerts(),
      notifications: this.apiService.getAllNotifications(),
      transactions: this.apiService.getAllTransactions()
    }).subscribe({
      next: ({ clients, accounts, alerts, notifications, transactions }) => {
        this.clients = clients;
        this.accounts = accounts;
        this.alerts = alerts;
        this.notifications = notifications;
        this.transactions = transactions;
        this.updateTabCounts();
        this.loading = false;
      },
      error: () => {
        this.errorMessage = 'Failed to load admin data. Ensure backend services are running.';
        this.loading = false;
      }
    });
  }

  // ===== ACCOUNT CRUD OPERATIONS =====

  openCreateAccountModal(): void {
    this.showCreateAccountModal = true;
    this.createAccountForm.reset({ accountType: 'CHECKING', balance: 0 });
  }

  closeCreateAccountModal(): void {
    this.showCreateAccountModal = false;
    this.createAccountForm.reset();
  }

  createAccount(): void {
    if (this.createAccountForm.invalid) {
      this.createAccountForm.markAllAsTouched();
      return;
    }

    this.createOperating = true;
    const formValue = this.createAccountForm.getRawValue();

    this.apiService.createAccount({
      clientId: formValue.clientId || '',
      accountNumber: formValue.accountNumber || '',
      accountType: formValue.accountType || 'CHECKING',
      balance: formValue.balance ?? 0
    }).subscribe({
      next: (newAccount) => {
        this.accounts.push(newAccount);
        this.updateTabCounts();
        this.successMessage = `Account created successfully for client ${newAccount.clientId}`;
        this.createOperating = false;
        this.closeCreateAccountModal();
        setTimeout(() => (this.successMessage = ''), 3000);
      },
      error: (err) => {
        this.createOperating = false;
        this.errorMessage = err?.error?.message || 'Failed to create account';
      }
    });
  }

  openUpdateAccountModal(account: Account): void {
    this.selectedAccount = account;
    this.showUpdateAccountModal = true;
    this.updateAccountForm.patchValue({
      balance: account.balance,
      status: account.status
    });
  }

  closeUpdateAccountModal(): void {
    this.showUpdateAccountModal = false;
    this.selectedAccount = null;
    this.updateAccountForm.reset();
  }

  updateAccount(): void {
    if (!this.selectedAccount || this.updateAccountForm.invalid) {
      this.updateAccountForm.markAllAsTouched();
      return;
    }

    this.updateOperating = true;
    const formValue = this.updateAccountForm.getRawValue();

    this.apiService.updateAccount(this.selectedAccount.id, {
      balance: formValue.balance ?? 0,
      status: formValue.status || 'ACTIVE'
    }).subscribe({
      next: (updatedAccount) => {
        const index = this.accounts.findIndex(a => a.id === updatedAccount.id);
        if (index > -1) {
          this.accounts[index] = updatedAccount;
        }
        this.updateTabCounts();
        this.successMessage = `Account ${updatedAccount.accountNumber} updated successfully`;
        this.updateOperating = false;
        this.closeUpdateAccountModal();
        setTimeout(() => (this.successMessage = ''), 3000);
      },
      error: (err) => {
        this.updateOperating = false;
        this.errorMessage = err?.error?.message || 'Failed to update account';
      }
    });
  }

  deleteAccount(accountId: string, accountNumber: string): void {
    if (!confirm(`Are you sure you want to delete account ${accountNumber}?`)) {
      return;
    }

    this.deleteOperating[accountId] = true;

    this.apiService.deleteAccount(accountId).subscribe({
      next: () => {
        this.accounts = this.accounts.filter(a => a.id !== accountId);
        this.updateTabCounts();
        this.successMessage = `Account ${accountNumber} deleted successfully`;
        this.deleteOperating[accountId] = false;
        setTimeout(() => (this.successMessage = ''), 3000);
      },
      error: (err) => {
        this.deleteOperating[accountId] = false;
        this.errorMessage = err?.error?.message || 'Failed to delete account';
      }
    });
  }

  // ===== UTILITY METHODS =====

  logout(): void {
    this.authService.logout();
  }

  formatDate(value?: string): string {
    return value ? new Date(value).toLocaleString() : '—';
  }

  formatAmount(value?: number): string {
    return value != null
      ? new Intl.NumberFormat('fr-MA', { style: 'currency', currency: 'MAD' }).format(value)
      : '—';
  }

  private updateTabCounts(): void {
    this.tabs = [
      { id: 'clients', label: 'Clients', count: this.clients.length },
      { id: 'accounts', label: 'Accounts', count: this.accounts.length },
      { id: 'alerts', label: 'Alerts', count: this.alerts.length },
      { id: 'notifications', label: 'Notifications', count: this.notifications.length },
      { id: 'transactions', label: 'Transactions', count: this.transactions.length }
    ];
  }
}
