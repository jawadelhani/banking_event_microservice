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

# Authentication with Keycloak

The API Gateway is protected using Keycloak OAuth2 Resource Server.

All requests to the microservices must pass through the Gateway and include a valid JWT access token.

## Keycloak

Default Realm

```
banking
```

Default Client

```
banking-client
```

Default Test User

```
Username: jawad
Password: 1234
```

---

## Obtain an Access Token

Send a POST request to:

```
http://localhost:8180/realms/banking/protocol/openid-connect/token
```

Headers

```
Content-Type: application/x-www-form-urlencoded
```

Body

```
grant_type=password
client_id=banking-client
username=jawad
password=1234
```

If the client is confidential, also include

```
client_secret=<your-client-secret>
```

A successful response returns

```json
{
  "access_token": "eyJhbGciOiJIUzI1NiIs..."
}
```

Copy the `access_token`.

---

## Test Protected APIs

Include the token in the Authorization header.

```
Authorization: Bearer <access_token>
```

Example requests

```
GET http://localhost:9090/account-service/accounts
```

```
GET http://localhost:9090/agency-service/alerts
```

```
GET http://localhost:9090/notification-service/notifications
```

```
GET http://localhost:9090/transaction-simulator-service/transactions
```

Without a valid JWT token the Gateway returns

```
401 Unauthorized
```

With a valid token the request is forwarded to the target microservice.

---

## Public Endpoints

The following endpoints do **not** require authentication:

```
http://localhost:8761
```

```
http://localhost:8888
```

```
http://localhost:9090/actuator/health
```

```
http://localhost:9090/actuator/gateway/routes
```

These endpoints are intended for service discovery, configuration, and monitoring.

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