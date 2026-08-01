export interface LoginRequest {
  username: string;
  password: string;
}

export interface LoginResponse {
  accessToken: string;
  refreshToken?: string;
  expiresIn?: number;
  tokenType?: string;
}

export interface RegisterRequest {
  username: string;
  password: string;
  firstName: string;
  lastName: string;
  cin: string;
  email: string;
  phone?: string;
  address?: string;
}

export interface Client {
  id: string;
  cin: string;
  fullName: string;
  email: string;
  phone?: string;
  allowNotifications?: boolean;
  monthlyIncome?: number;
  createdAt?: string;
}

export interface Account {
  id: string;
  clientId: string;
  accountNumber: string;
  balance: number;
  accountType: string;
  status: string;
}

export interface AgencyAlert {
  id: string;
  clientId: string;
  txId?: string;
  criticality: string;
  seenByAgent: boolean;
  createdAt?: string;
}

export interface Notification {
  id: string;
  clientId: string;
  txId?: string;
  channel: string;
  message: string;
  status: string;
  sentAt?: string;
}

export interface Transaction {
  id: string;
  accountId: string;
  amount: number;
  type: string;
  createdAt?: string;
}
