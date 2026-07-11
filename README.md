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
- Git
- IntelliJ IDEA (optional)

---

# Clone the project

```bash
git clonehttps://github.com/jawadelhani/banking_event_microservice
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

## 3. API Gateway

Run:

```
gateway-service
```

Available at

```
http://localhost:9090
```

---

## 4. Account Service

Run:

```
account-service
```

---

## 5. Agency Service

Run:

```
agency-service
```

---

## 6. Notification Service

Run:

```
notification-service
```

---

## 7. Transaction Simulator

Run:

```
transaction-simulator-service
```

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
# Authentication & Role-Based Access Control (RBAC)

The Banking System uses **Keycloak** for authentication and authorization.

Security is implemented as follows:

- Keycloak authenticates users.
- The API Gateway validates JWT access tokens.
- Each microservice enforces authorization based on user roles.
- All client requests must pass through the Gateway.

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

```
Username: jawad
Password: 1234
Role: CLIENT
```

---

## Obtain an Access Token

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
  "access_token": "eyJhbGciOi..."
}
```

Copy the value of **access_token**.

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

# RBAC Testing

## 1. Admin Access

Login as

```
admin
```

Obtain an access token.

The following requests should succeed.

### Account Service

```
GET /account-service/accounts
```

```
POST /account-service/accounts
```

```
PUT /account-service/accounts/{id}
```

```
DELETE /account-service/accounts/{id}
```

### Agency Service

```
GET /agency-service/alerts
```

```
POST /agency-service/alerts
```

```
PATCH /agency-service/alerts/{id}/seen
```

```
DELETE /agency-service/alerts/{id}
```

Expected

```
200 OK
```

or

```
201 Created
```

---

## 2. Client Access

Login as

```
jawad
```

Obtain an access token.

The client can access only their own banking information.

Example

```
GET /account-service/accounts/client/{clientId}
```

Expected

```
200 OK
```

The client should NOT be able to access administrative endpoints such as

```
GET /account-service/accounts
```

```
POST /account-service/accounts
```

```
DELETE /account-service/accounts/{id}
```

```
GET /agency-service/alerts
```

```
POST /agency-service/alerts
```

Expected response

```
403 Forbidden
```

---

## Authentication Errors

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

### Invalid Token

Using an expired or invalid JWT

Response

```
401 Unauthorized
```

---

### Insufficient Permissions

Using a CLIENT token to access an ADMIN endpoint

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
                 | JWT Validation|
                 +-------+-------+
                         |
         +---------------+---------------+
         |               |               |
         v               v               v
  Account Service   Agency Service   Notification Service
      RBAC              RBAC               RBAC
```

Authentication is performed once at the Gateway.

Authorization is enforced inside each microservice using Spring Security roles (`ROLE_ADMIN`, `ROLE_CLIENT`).
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
- ✅ Jenkins CI
- ✅ SonarQube
- ✅ OWASP Dependency Check
- ✅ Trivy

---

# Next Steps

- Keycloak Authentication
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