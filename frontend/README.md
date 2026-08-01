# Al Barid Bank - Frontend Portal

Modern, responsive Angular 17 frontend for the Al Barid Bank microservices system. Built with standalone components, reactive forms, and role-based access control using JWT authentication.

## 🎨 Features

### Authentication & Authorization
- **JWT-Based Login**: Secure sign-in with username and password
- **User Registration**: Self-service account creation with validation
- **Role-Based Access**: Separate dashboards for Admin and Client roles
- **Automatic Redirection**: Smart routing based on user role
- **Session Management**: Token storage and automatic expiration handling

### Admin Dashboard
- **Client Management**: View all registered clients
- **Account Monitoring**: Inspect all bank accounts across clients
- **Agency Alerts**: Monitor system alerts with criticality levels
- **Notification Center**: Track all system and user notifications
- **Transaction History**: View all transactions in real-time
- **Data Refresh**: Manual refresh button for up-to-date information

### Client Dashboard
- **Personal Profile**: View account details and membership info
- **Account Management**: See all personal accounts with balances
- **Alert System**: Receive and view personal alerts
- **Notification Inbox**: Check personal notifications
- **Transaction Tracking**: Monitor personal transaction history
- **Balance Overview**: Quick view of total account balance

### Design
- **Yellow Theme**: Al Barid Bank brand colors (Yellow #FDB913 with Charcoal accents)
- **Responsive Design**: Works seamlessly on desktop, tablet, and mobile
- **Modern UI**: Clean cards, smooth transitions, and professional typography
- **Accessibility**: WCAG-compliant with semantic HTML
- **Dark Mode Ready**: CSS variables support for theme customization

## 🚀 Quick Start

### Prerequisites
- Node.js 18+ and npm 9+
- Running API Gateway at `http://localhost:9090`
- Keycloak authentication server (for production)

### Installation

```bash
cd frontend
npm install
```

### Development Server

```bash
npm start
# or
ng serve
```

Navigate to `http://localhost:4200/`. The app will auto-reload on file changes.

### Production Build

```bash
npm run build
# or
ng build
```

Build artifacts will be stored in `dist/frontend/`.

## 📁 Project Structure

```
frontend/
├── src/
│   ├── app/
│   │   ├── auth/
│   │   │   ├── login/          # Login page component
│   │   │   └── register/       # Registration page component
│   │   ├── dashboards/
│   │   │   ├── admin/          # Admin dashboard (all data)
│   │   │   └── client/         # Client dashboard (personal data)
│   │   ├── services/
│   │   │   ├── auth.service.ts # Authentication service
│   │   │   └── api.service.ts  # API calls to microservices
│   │   ├── guards/
│   │   │   └── auth.guard.ts   # Role-based route guards
│   │   ├── interceptors/
│   │   │   └── auth.interceptor.ts # JWT token injection
│   │   ├── models/
│   │   │   └── banking.models.ts   # TypeScript interfaces
│   │   ├── app.routes.ts       # Route definitions
│   │   ├── app.config.ts       # Application configuration
│   │   └── app.component.ts    # Root component
│   ├── styles.css              # Global styles with theme
│   ├── main.ts                 # Application entry point
│   ├── index.html              # HTML template
│   ├── proxy.conf.json         # API proxy configuration
│   └── assets/
│       └── al-barid-logo.png   # Bank branding
├── angular.json                # Angular CLI configuration
├── tsconfig.json               # TypeScript configuration
└── package.json                # Dependencies
```

## 🔌 API Integration

All API requests are proxied through the API Gateway at `http://localhost:9090`.

### Configured Services
- **Account Service** (`/account-service`): User authentication, account management
- **Agency Service** (`/agency-service`): Agency alerts and monitoring
- **Notification Service** (`/notification-service`): Notifications and messaging
- **Transaction Simulator** (`/transaction-simulator-service`): Transaction history and simulation

### Example API Endpoints Used
```typescript
// Authentication
POST /account-service/auth/login
POST /account-service/auth/register

// User Data
GET /account-service/clients/me
GET /account-service/accounts/me
GET /account-service/clients (admin only)
GET /account-service/accounts (admin only)

// Notifications
GET /notification-service/notifications/me
GET /notification-service/notifications (admin only)

// Transactions
GET /transaction-simulator-service/transactions/me
GET /transaction-simulator-service/transactions (admin only)

// Agency
GET /agency-service/alerts/me
GET /agency-service/alerts (admin only)
```

## 🛡️ Security Features

1. **JWT Authentication**: Token-based authentication with role claims
2. **Auth Interceptor**: Automatically adds Bearer token to all requests
3. **Route Guards**: Prevents unauthorized access to protected routes
4. **Role Extraction**: Decodes JWT to extract user roles from Keycloak
5. **Token Storage**: Secure localStorage management with logout cleanup

## 🎯 User Flows

### Admin User
1. Login with admin credentials
2. Redirected to `/admin` dashboard
3. Can switch between tabs: Clients, Accounts, Alerts, Notifications, Transactions
4. View comprehensive system-wide data
5. Logout clears token and session

### Client User
1. Register for new account or login with credentials
2. Redirected to `/client` dashboard
3. View personal profile, accounts, alerts, notifications, transactions
4. Total balance calculation across all accounts
5. Logout clears token and session

### Guest User
1. Can only access `/login` and `/register` routes
2. Auto-redirected to login if trying to access protected routes
3. After successful login/register, redirected to appropriate dashboard

## 🎨 Theme Customization

The application uses CSS variables for easy theming. Edit `src/styles.css` to customize:

```css
:root {
  --primary-color: #FDB913;      /* Yellow */
  --primary-dark: #e5a305;       /* Darker Yellow */
  --secondary-color: #4A4A4A;    /* Charcoal */
  --background-light: #F7F9FC;   /* Light Gray */
  --text-dark: #2C3E50;          /* Dark Text */
  --text-light: #7F8C8D;         /* Light Text */
  --error: #E74C3C;              /* Red */
  --success: #2ECC71;            /* Green */
}
```

## 📱 Responsive Breakpoints

- **Mobile**: < 640px
- **Tablet**: 640px - 900px
- **Desktop**: > 900px

## 🧪 Testing

```bash
npm test
# or
ng test
```

Runs the test suite using Karma and Jasmine.

## 📦 Dependencies

### Core
- **@angular/core**: 17.3.0
- **@angular/common**: 17.3.0
- **@angular/router**: 17.3.0
- **@angular/forms**: 17.3.0

### Utilities
- **rxjs**: 7.8.0 (Reactive programming)
- **TypeScript**: 5.4.2

## 🚦 Development Workflows

### Adding a New Component
```bash
ng generate component components/my-component
# or with routing
ng generate component auth/my-auth --route my-route
```

### Creating a New Service
```bash
ng generate service services/my-service
```

### Linting & Formatting
```bash
ng lint          # Check code quality
```

## 🔧 Environment Variables

Create a `.env` file (optional) for API configuration:
```
NG_API_URL=http://localhost:9090
```

## 🐛 Troubleshooting

### Blank Login Page
- Ensure Al Barid logo file exists at `src/assets/al-barid-logo.png`
- Check browser console for errors

### "Cannot GET /api/*"
- Verify API Gateway is running at `http://localhost:9090`
- Check proxy configuration in `src/proxy.conf.json`
- Run `ng serve --proxy-config src/proxy.conf.json`

### Authentication Token Issues
- Clear localStorage: `localStorage.clear()` in browser console
- Refresh the page
- Logout and login again

### CORS Errors
- Ensure API Gateway has CORS properly configured
- Gateway should allow requests from `http://localhost:4200`

## 📖 Further Reading

- [Angular Documentation](https://angular.io/docs)
- [Angular CLI Guide](https://angular.io/cli)
- [RxJS Documentation](https://rxjs.dev/)
- [TypeScript Handbook](https://www.typescriptlang.org/docs/)

## 📄 License

Part of Al Barid Bank Microservices System
