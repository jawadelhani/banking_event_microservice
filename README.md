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
  "profiles": ["default"],
  ...
}
```

---

# Test Services Through Gateway

Example endpoints:

```
http://localhost:9090/account-service/api/accounts
```

```
http://localhost:9090/agency-service/api/agencies
```

```
http://localhost:9090/notification-service/api/notifications
```

```
http://localhost:9090/transaction-simulator-service/api/transactions
```

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