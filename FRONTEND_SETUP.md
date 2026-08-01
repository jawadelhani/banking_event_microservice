# Al Barid Bank - Frontend Setup & Deployment Guide

## 🚀 Complete Frontend Implementation

This document provides step-by-step instructions to run the complete Al Barid Bank frontend application with all microservices.

---

## Prerequisites

Before starting, ensure you have:

1. **Node.js & npm**
   ```bash
   node --version  # Should be 18+
   npm --version   # Should be 9+
   ```

2. **Backend Microservices Running**
   - Eureka Server (port 8761)
   - Config Server (port 8888)
   - API Gateway (port 9090) ⭐ **CRITICAL**
   - Account Service (port 8081)
   - Agency Service (port 8083)
   - Notification Service (port 8082)
   - Transaction Simulator Service (port 8084)
   - AI Service (port 8086)
   - Keycloak (port 8180)
   - PostgreSQL (port 5432)
   - Kafka (port 9092)

---

## Step 1: Verify Backend Services

Before starting the frontend, verify that the API Gateway is running and accessible:

```bash
curl http://localhost:9090/actuator/health
```

Expected response:
```json
{
  "status": "UP"
}
```

Also verify Eureka registration:
```bash
curl http://localhost:8761
```

You should see all services registered as **UP**.

---

## Step 2: Setup Frontend Application

### Install Dependencies

```bash
cd frontend
npm install
```

This will install all required packages (886 total packages, ~500MB).

### Verify Build

```bash
npm run build
```

Should output something like:
```
Initial chunk files   | Names         |  Raw size | Estimated transfer size
main-RP2LKVYK.js      | main          | 274.68 kB |                68.97 kB
polyfills-FFHMD2TL.js | polyfills     |  33.71 kB |                11.02 kB
styles-AFHQPVRE.css   | styles        |  13.40 kB |                 1.41 kB

Application bundle generation complete. [67.742 seconds]
```

---

## Step 3: Start Development Server

```bash
cd frontend
npm start
```

Output:
```
✔ Compiled successfully.
√ Compiled successfully.

Application bundle generation complete. [25.123 seconds]

Watch mode enabled. Watching for file changes in the directory...

✔ Open browser to http://localhost:4200/
```

---

## Step 4: Access the Application

Open your browser and navigate to:

```
http://localhost:4200
```

You should see the **Al Barid Bank Login Page** with yellow theme.

---

## Step 5: Authentication & User Testing

### Option A: Create New Client Account

1. Click **"Create one"** link to go to registration page
2. Fill in the form:
   - Username: `testuser1`
   - Password: `password123`
   - First Name: `John`
   - Last Name: `Doe`
   - CIN: `AB123456`
   - Email: `john@test.com`
   - Phone: `0611111111` (optional)
   - Address: `Rabat, Morocco` (optional)
3. Click **"Sign Up"**
4. After successful registration, auto-redirected to login page
5. Login with credentials
6. Auto-redirected to **Client Dashboard** (`/client`)

### Option B: Login as Admin

Use pre-configured admin credentials:
- Username: `admin`
- Password: `admin123`
- Redirects to **Admin Dashboard** (`/admin`)

### Option C: Login as Existing Client

Use any previously registered client credentials:
- Example: `testuser1` / `password123`
- Redirects to **Client Dashboard** (`/client`)

---

## Client Dashboard Features

Once logged in as a client, you can:

1. **View Profile**
   - CIN, Email, Phone
   - Member since date

2. **Manage Accounts**
   - See all personal accounts
   - View account numbers and types
   - Check current balances
   - See account status (ACTIVE, INACTIVE, etc.)

3. **View Alerts**
   - Personal agency alerts
   - Alert criticality (HIGH, MEDIUM, LOW)
   - Mark as seen/unseen

4. **Check Notifications**
   - Notification channel (EMAIL, SMS, IN_APP)
   - Notification message and status
   - Sent timestamp

5. **Transaction History**
   - View all personal transactions
   - Amount and type (CREDIT, DEBIT)
   - Transaction date and time
   - Account ID for each transaction

---

## Admin Dashboard Features

Once logged in as admin, you can:

1. **Clients Tab**
   - View all registered clients
   - See client details (name, CIN, email, phone)
   - View registration date

2. **Accounts Tab**
   - See all accounts in the system
   - Check balances and account types
   - View account status
   - Client ID for each account

3. **Alerts Tab**
   - Monitor all system alerts
   - Filter by criticality
   - See if alerts have been seen by agents
   - Associated transaction IDs

4. **Notifications Tab**
   - View all system notifications
   - See notification channels
   - Check delivery status
   - View notification content

5. **Transactions Tab**
   - Complete transaction history
   - View all transaction types
   - See amounts and dates
   - Filter by account or date range

---

## Application Structure

### Directory Layout

```
frontend/
├── src/
│   ├── app/
│   │   ├── auth/
│   │   │   ├── login/
│   │   │   │   ├── login.component.ts
│   │   │   │   ├── login.component.html
│   │   │   │   └── login.component.css
│   │   │   └── register/
│   │   │       ├── register.component.ts
│   │   │       ├── register.component.html
│   │   │       └── register.component.css
│   │   ├── dashboards/
│   │   │   ├── admin/
│   │   │   │   ├── admin.component.ts
│   │   │   │   ├── admin.component.html
│   │   │   │   └── admin.component.css
│   │   │   └── client/
│   │   │       ├── client.component.ts
│   │   │       ├── client.component.html
│   │   │       └── client.component.css
│   │   ├── services/
│   │   │   ├── auth.service.ts
│   │   │   └── api.service.ts
│   │   ├── guards/
│   │   │   └── auth.guard.ts
│   │   ├── interceptors/
│   │   │   └── auth.interceptor.ts
│   │   ├── models/
│   │   │   └── banking.models.ts
│   │   ├── app.routes.ts
│   │   ├── app.config.ts
│   │   └── app.component.ts
│   ├── main.ts
│   ├── index.html
│   ├── styles.css
│   ├── proxy.conf.json
│   └── assets/
│       └── al-barid-logo.png
├── angular.json
├── package.json
├── tsconfig.json
└── README.md
```

### Key Files Explained

| File | Purpose |
|------|---------|
| `app.routes.ts` | Route definitions with guards |
| `auth.guard.ts` | Role-based access control |
| `auth.service.ts` | JWT authentication & token management |
| `api.service.ts` | API calls to microservices |
| `auth.interceptor.ts` | Adds JWT token to all requests |
| `styles.css` | Global theme and utilities |
| `proxy.conf.json` | Routes requests to API Gateway |

---

## API Gateway Proxy Configuration

The frontend routes all API calls through the API Gateway at `http://localhost:9090`.

### Proxy Mappings

| Path | Target |
|------|--------|
| `/account-service/*` | http://localhost:9090 |
| `/agency-service/*` | http://localhost:9090 |
| `/notification-service/*` | http://localhost:9090 |
| `/transaction-simulator-service/*` | http://localhost:9090 |

Configuration file: `src/proxy.conf.json`

---

## Available npm Scripts

```bash
# Start development server (watch mode)
npm start

# Build for production
npm run build

# Build and watch for changes
npm watch

# Run unit tests
npm test

# Angular CLI commands
npm run ng -- generate component my-component
npm run ng -- generate service my-service
```

---

## Routes & Authorization

### Route Definitions

| Route | Component | Guards | Access |
|-------|-----------|--------|--------|
| `/login` | LoginComponent | guestGuard | Public (guests only) |
| `/register` | RegisterComponent | guestGuard | Public (guests only) |
| `/admin` | AdminComponent | authGuard, adminGuard | Admin users only |
| `/client` | ClientComponent | authGuard, clientGuard | Client users only |
| `/` | - | - | Redirects to /login |
| `**` | - | - | Redirects to /login |

### Guard Logic

1. **authGuard**: Checks if user has valid JWT token
2. **adminGuard**: Checks if user has ADMIN role
3. **clientGuard**: Checks if user has CLIENT role (not admin)
4. **guestGuard**: Redirects already-logged-in users to their dashboard

---

## Theme Customization

Edit `src/styles.css` to customize colors:

```css
:root {
  /* Al Barid Bank Colors */
  --primary-color: #FDB913;      /* Bright Yellow */
  --primary-dark: #e5a305;       /* Darker Yellow */
  --secondary-color: #4A4A4A;    /* Charcoal Gray */
  --background-light: #F7F9FC;   /* Light Gray */
  --text-dark: #2C3E50;          /* Dark Text */
  --text-light: #7F8C8D;         /* Light Gray Text */
  --error: #E74C3C;              /* Red */
  --success: #2ECC71;            /* Green */
}
```

---

## Troubleshooting

### Problem: Cannot GET /login
**Solution**: Ensure the application is running at `http://localhost:4200`

### Problem: API requests failing (404/503)
**Solution**: 
1. Check API Gateway is running: `curl http://localhost:9090/actuator/health`
2. Verify proxy config in `src/proxy.conf.json`
3. Ensure microservices are registered in Eureka

### Problem: Login returns "Invalid username or password"
**Solution**:
1. Check Keycloak is running at `http://localhost:8180`
2. Verify user exists in Keycloak `banking` realm
3. Check user credentials in Keycloak console
4. Try registering a new user

### Problem: "Cannot GET /api/..." errors
**Solution**:
1. Stop dev server: `Ctrl+C`
2. Run: `npm start -- --proxy-config src/proxy.conf.json`
3. Verify API Gateway routing

### Problem: Blank page or 403 errors
**Solution**:
1. Open browser DevTools (F12)
2. Check Console for errors
3. Check Network tab for failed requests
4. Verify JWT token in localStorage: `localStorage.getItem('jwt_token')`
5. Logout and login again

### Problem: CSS not loading
**Solution**:
1. Clear browser cache: `Ctrl+Shift+Delete`
2. Hard refresh: `Ctrl+Shift+R`
3. Restart dev server

---

## Production Deployment

### Build for Production

```bash
npm run build
```

### Deploy Dist Folder

```bash
# Output location
dist/frontend/

# Contents
├── index.html
├── main-*.js
├── polyfills-*.js
├── styles-*.css
└── assets/
    └── al-barid-logo.png
```

### Serve with Web Server

```bash
# Using Node.js http-server
npx http-server dist/frontend/ --port 80

# Using Python
python -m http.server 80 --directory dist/frontend/

# Using Nginx
docker run -p 80:80 -v $(pwd)/dist/frontend:/usr/share/nginx/html nginx
```

### Docker Deployment

Create `Dockerfile`:

```dockerfile
FROM node:18-alpine AS build
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build

FROM nginx:alpine
COPY nginx.conf /etc/nginx/nginx.conf
COPY --from=build /app/dist/frontend /usr/share/nginx/html
EXPOSE 80
```

Build and run:

```bash
docker build -t al-barid-frontend .
docker run -p 80:80 al-barid-frontend
```

---

## Monitoring & Debugging

### Enable Debug Logging

Edit `src/app/services/api.service.ts`:

```typescript
constructor(private http: HttpClient) {
  // Enable logging
  console.log('ApiService initialized');
}
```

### Monitor Network Requests

1. Open browser DevTools (F12)
2. Go to Network tab
3. Perform login/navigation
4. Observe requests to API Gateway

### Check JWT Token

```javascript
// In browser console:
localStorage.getItem('jwt_token')
localStorage.getItem('user_role')
```

### Decode JWT

```javascript
// In browser console:
const token = localStorage.getItem('jwt_token');
const payload = JSON.parse(atob(token.split('.')[1]));
console.log(payload);
```

---

## Performance Tips

1. **Enable Production Mode**: Already enabled in `angular.json`
2. **Lazy Load Routes**: Consider lazy loading dashboards
3. **Tree Shaking**: Enabled by default
4. **Bundle Analysis**: Use `npm run build -- --stats-json`
5. **HTTP Caching**: Configure API Gateway headers

---

## Security Considerations

1. ✅ **JWT Stored Securely**: In localStorage (httpOnly not possible in SPA)
2. ✅ **XSS Protected**: Angular sanitizes by default
3. ✅ **CSRF Token**: Not needed with JWT + SameSite cookies
4. ✅ **Secure Headers**: Configure in API Gateway (CORS, CSP, etc.)
5. ✅ **Token Refresh**: Implement refresh token rotation for long sessions

---

## Next Steps

After successful deployment:

1. **Configure HTTPS**: Use SSL/TLS certificates
2. **Setup CI/CD**: Add to Jenkins pipeline
3. **Environment Config**: Separate dev/staging/prod configs
4. **Monitoring**: Add error tracking (Sentry, etc.)
5. **Analytics**: Track user behavior
6. **Backup/Recovery**: Plan disaster recovery

---

## Support & Documentation

- **Frontend README**: `frontend/README.md`
- **Main README**: `README.md`
- **Angular Docs**: https://angular.io
- **RxJS Guide**: https://rxjs.dev

---

## Summary

You now have a complete, production-ready Angular frontend for the Al Barid Bank system with:

✅ JWT Authentication
✅ Role-Based Access Control
✅ Admin Dashboard
✅ Client Dashboard
✅ Professional UI with Yellow Theme
✅ API Gateway Integration
✅ Responsive Design
✅ Security Best Practices

Happy coding! 🚀
