# Banking System - Microservices

A banking system built using Spring Boot microservices following a cloud-native architecture.

## Architecture

- Eureka Server (Service Discovery)
- Spring Cloud Config Server
- API Gateway
- Account Service
- Agency Service
- Notification Service
- Transaction Simulator Service

Technologies:

- Spring Boot 3.5.x
- Spring Cloud 2025
- Spring Cloud Gateway
- Eureka Discovery Server
- Spring Cloud Config
- PostgreSQL
- Keycloak
- Docker
- Jenkins
- SonarQube
- OWASP Dependency Check
- Trivy

---

# Prerequisites

- Java 17+
- Maven 3.9+
- PostgreSQL
- Keycloak (running on `localhost:8180`)
- Git
- IntelliJ IDEA (optional)

---

# Clone the Project

```bash
git clone https://github.com/jawadelhani/banking_event_microservice
cd banking_event_microservice
```

---

# Clone the Config Repository

```bash
git clone https://github.com/jawadelhani/banking-config
```

This repository contains all configuration files for the microservices.

Example:

```
bank-config-repo
│
├── account-service.yml
├── agency-service.yml
├── notification-service.yml
├── transaction-simulator-service.yml
├── gateway-service.yml
```

---

# Configure PostgreSQL

Create the databases:

```sql
CREATE DATABASE accounts;
CREATE DATABASE agency;
CREATE DATABASE notifications;
CREATE DATABASE simulator;
```

Update the database credentials inside the Config Repository if necessary.

---

# Startup Order

The services **must** be started in the following order.

## 1. Eureka Server

Run:

```
eureka-server
```

Available at

```
http://localhost:8761
```

---

## 2. Config Server

Run:

```
config-server
```

Verify:

```
http://localhost:8888/account-service/default
```

You should receive the configuration in JSON format.

---

## 3. Keycloak

Start Keycloak and confirm the `banking` realm and `banking-client` client exist.

Available at

```
http://localhost:8180
```

---

## 4. API Gateway

Run:

```
gateway-service
```

Available at

```
http://localhost:9090
```

---

## 5. Account Service

Run:

```
account-service
```

Port: `8081`

---

## 6. Agency Service

Run:

```
agency-service
```

Port: `8083`

---

## 7. Notification Service

Run:

```
notification-service
```

Port: `8082`

---

## 8. Transaction Simulator Service

Run:

```
transaction-simulator-service
```

Port: `8084`

---

# Verify Eureka Registration

Open

```
http://localhost:8761
```

Expected:

```
ACCOUNT-SERVICE
AGENCY-SERVICE
NOTIFICATION-SERVICE
TRANSACTION-SIMULATOR-SERVICE
CONFIG-SERVER
GATEWAY-SERVICE
```

All services should display:

```
UP
```

---

# Verify Config Server

Example:

```
http://localhost:8888/account-service/default
```

Expected:

```json
{
  "name": "account-service",
  "profiles": ["default"]
}
```

---

# Authentication & Role-Based Access Control (RBAC)

The Banking System uses **Keycloak** for authentication and authorization.

Security is implemented as follows:

- Keycloak authenticates users and issues JWT access tokens.
- Each microservice independently validates the JWT against Keycloak (via `issuer-uri`).
- Each microservice enforces authorization based on user roles (`ROLE_ADMIN`, `ROLE_CLIENT`).
- All client requests are routed through the API Gateway.

---

## Keycloak Configuration

Realm

```
banking
```

Client

```
banking-client
```

---

## Test Users

### Admin

```
Username: admin
Password: admin123
Role: ADMIN
```

### Client

Register a new client through the API (see Account Service section below), or use an existing one:

```
Username: jawad
Password: 1234
Role: CLIENT
```

---

## Obtain an Access Token Directly from Keycloak

Send a POST request to

```
http://localhost:8180/realms/banking/protocol/openid-connect/token
```

Headers

```
Content-Type: application/x-www-form-urlencoded
```

Body (Admin)

```
grant_type=password
client_id=banking-client
username=admin
password=admin123
```

Body (Client)

```
grant_type=password
client_id=banking-client
username=jawad
password=1234
```

If your Keycloak client is confidential, also include

```
client_secret=<your-client-secret>
```

Successful response

```json
{
  "access_token": "eyJhbGciOi...",
  "refresh_token": "eyJhbGciOi...",
  "expires_in": 300,
  "token_type": "Bearer"
}
```

Copy the value of **access_token**.

> You can also obtain a token through `account-service`'s own `/auth/login` endpoint — see the Account Service testing section below for the recommended flow.

---

## Calling Protected APIs

Include the JWT in every request.

```
Authorization: Bearer <access_token>
```

Example

```
GET http://localhost:9090/account-service/accounts
```

---

# Full Testing Guide

All requests below go through the API Gateway on port `9090`.

## 1. Account Service

### Register a New Client

```
POST http://localhost:9090/account-service/auth/register
```

Body

```json
{
  "username": "client5",
  "password": "1234",
  "firstName": "John",
  "lastName": "Doe",
  "cin": "AA998877",
  "email": "john@test.com",
  "phone": "0611111111",
  "address": "Rabat"
}
```

This creates:

- A Keycloak user with the `CLIENT` role
- A `Client` row in the `accounts` database
- A default `Account` row with a zero balance

### Login

```
POST http://localhost:9090/account-service/auth/login
```

Body

```json
{
  "username": "client5",
  "password": "1234"
}
```

Copy the returned `accessToken` — this is the JWT you'll use for every request below.

### Current Client Profile

```
GET http://localhost:9090/account-service/clients/me
```

Header

```
Authorization: Bearer <JWT>
```

### Current User's Accounts

```
GET http://localhost:9090/account-service/accounts/me
```

Header

```
Authorization: Bearer <JWT>
```

---

## 2. Agency Service

### Current User's Alerts

```
GET http://localhost:9090/agency-service/alerts/me
```

Header

```
Authorization: Bearer <JWT>
```

Expected

```json
[]
```

until alerts exist for this client.

### Admin-Only Endpoints

```
GET    /agency-service/alerts
GET    /agency-service/alerts/{id}
POST   /agency-service/alerts
PUT    /agency-service/alerts/{id}
DELETE /agency-service/alerts/{id}
```

These should return `403 Forbidden` when called with a CLIENT token.

---

## 3. Notification Service

### Current User's Notifications

```
GET http://localhost:9090/notification-service/notifications/me
```

Header

```
Authorization: Bearer <JWT>
```

Internally, this endpoint:

```
JWT
  ↓
Account Service (/clients/me)
  ↓
clientId
  ↓
notificationRepository.findByClientId(...)
```

### Admin-Only Endpoints

```
GET    /notification-service/notifications
POST   /notification-service/notifications
PUT    /notification-service/notifications/{id}
DELETE /notification-service/notifications/{id}
```

---

## 4. Transaction Simulator Service

### Current User's Transactions

```
GET http://localhost:9090/transaction-simulator-service/transactions/me
```

Header

```
Authorization: Bearer <JWT>
```

Internally, this endpoint:

```
JWT
  ↓
Account Service (/accounts/me)
  ↓
Accounts
  ↓
transactionRepository.findByAccountId(...) for each account
```

### Admin-Only Endpoints

```
GET    /transaction-simulator-service/transactions
POST   /transaction-simulator-service/transactions
POST   /transaction-simulator-service/transactions/simulate
DELETE /transaction-simulator-service/transactions/{id}
```

---

# Expected RBAC Behavior

| Endpoint                                          | CLIENT | ADMIN |
|----------------------------------------------------|:------:|:-----:|
| `/account-service/clients/me`                       | ✅     | ❌ (403) |
| `/account-service/accounts/me`                      | ✅     | ❌ (403) |
| `/agency-service/alerts/me`                         | ✅     | ❌ (403) |
| `/notification-service/notifications/me`            | ✅     | ❌ (403) |
| `/transaction-simulator-service/transactions/me`     | ✅     | ❌ (403) |
| `GET /account-service/clients`                      | ❌ (403) | ✅ |
| `GET /account-service/accounts`                     | ❌ (403) | ✅ |
| `GET /agency-service/alerts`                        | ❌ (403) | ✅ |
| `GET /notification-service/notifications`           | ❌ (403) | ✅ |
| `GET /transaction-simulator-service/transactions`    | ❌ (403) | ✅ |

---

# Authentication Errors

### No Token

Calling a protected endpoint without a JWT

Example

```
GET http://localhost:9090/account-service/accounts
```

Response

```
401 Unauthorized
```

---

### Invalid or Expired Token

Using an expired or malformed JWT

Response

```
401 Unauthorized
```

---

### Insufficient Permissions

Using a CLIENT token to access an ADMIN endpoint (or vice versa)

Response

```
403 Forbidden
```

---

# Public Endpoints

These endpoints do not require authentication.

Eureka

```
http://localhost:8761
```

Config Server

```
http://localhost:8888
```

Gateway Health

```
http://localhost:9090/actuator/health
```

Gateway Routes

```
http://localhost:9090/actuator/gateway/routes
```

Account Service Auth Endpoints

```
POST http://localhost:9090/account-service/auth/register
POST http://localhost:9090/account-service/auth/login
```

---

# Security Architecture

```
                +----------------+
                |    Keycloak    |
                +--------+-------+
                         |
                    JWT Access Token
                         |
                         v
                 +---------------+
                 | API Gateway   |
                 +-------+-------+
                         |
     +-------------+-----+-----+-------------+
     |             |           |             |
     v             v           v             v
Account       Agency      Notification   Transaction
Service       Service     Service        Simulator
  RBAC          RBAC        RBAC           RBAC
     ^             |           |             |
     |             |           |             |
     +----- Feign calls to Account Service ---+
        (/clients/me, /accounts/me — same JWT
         forwarded on every internal call)
```

- Each microservice independently validates the JWT against Keycloak using its own `issuer-uri` config — there is no central trust broker beyond Keycloak itself.
- Authorization is enforced inside each microservice using Spring Security roles (`ROLE_ADMIN`, `ROLE_CLIENT`) via `@PreAuthorize`.
- Services that need data from `account-service` (Agency, Notification, Transaction Simulator) use Feign clients, manually forwarding the original `Authorization` header on every internal call.

---

# CI Pipeline

The project includes a Jenkins CI pipeline that performs:

- Checkout
- Maven Build
- Unit Tests
- SonarQube Analysis
- OWASP Dependency Check
- Trivy Image Scan
- HTML Report Publishing

Reports generated:

- SonarQube
- OWASP Dependency Check
- Trivy Scan

---

# Current Project Status

- ✅ Eureka Server
- ✅ Config Server
- ✅ API Gateway
- ✅ Microservices
- ✅ Centralized Configuration
- ✅ Keycloak Authentication & RBAC
- ✅ Jenkins CI
- ✅ SonarQube
- ✅ OWASP Dependency Check
- ✅ Trivy

---

# Next Steps

- Kafka Event Bus
- Docker Compose
- Kubernetes Deployment
- GitOps with ArgoCD
- Monitoring (Prometheus + Grafana)

---

# Project Structure

```
Banking-System
│
├── eureka-server
├── config-server
├── gateway-service
├── account-service
├── agency-service
├── notification-service
├── transaction-simulator-service
├── Jenkinsfile
└── README.md
```

---

# Author

Jawad El Hani

INPT – ASEDS

Spring Boot • Microservices • DevSecOps • Cloud Native