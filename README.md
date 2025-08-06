# PayPal Integration Project

## 📌 Project Overview
This project implements a **Payment System** for an e-commerce-like client application, enabling customers to pay securely using **PayPal Standard Checkout**.  
It follows a **Microservices Architecture** with three services:

1. **paypal-provider-service** → Handles PayPal API calls (Create Order, Capture Order, Get Order).
2. **paypal-processing-service** → Manages payment flow logic, reconciliation, and database status updates.
3. **eureka-service-registry** → Registers all services for service discovery.

The system ensures **secure, fault-tolerant, and scalable** payment processing with reconciliation logic and a **30-minute pending payment window**.

---

## 🏗 Architecture
```mermaid
graph TD;
    Buyer -->|Checkout Request| ProcessingService
    ProcessingService -->|API Call| ProviderService
    ProviderService -->|REST API| PayPal[PayPal API]
    ProcessingService -->|Status Updates| Database
    ProcessingService -->|Service Registration| Eureka
    ProviderService -->|Service Registration| Eureka
💻 Tech Stack
Backend: Java, Spring Boot, Spring MVC

Microservices: Eureka Service Registry, Feign Client, RestTemplate

Database: MySQL

Cache: Redis

API Docs: Postman Collections

Cloud: AWS EC2, RDS, Secrets Manager

Security: OAuth 2.0 (Client Credentials)

Build Tool: Maven

Version Control: Git, GitHub

Testing: JUnit, Mockito
📂 Microservices
paypal-provider-service → Integrates with PayPal APIs.

paypal-processing-service → Handles payment processing & reconciliation.

eureka-service-registry → Service registry for microservices.

🔄 Standard PayPal Checkout Flow
Buyer clicks "Pay with PayPal" on the client application.

Front-end calls paypal-processing-service to initiate payment.

Processing service requests order creation from paypal-provider-service.

Provider service calls PayPal Orders API (Create Order).

Buyer logs in to PayPal & approves the payment.

Processing service calls provider service to capture payment.

Provider service calls PayPal Orders API (Capture Order).

Status is updated in the database (success/failure).

If not completed in 30 mins, the system marks payment as failed.

📈 API Flow Example
POST /api/payments/create

Calls PayPal API to create order

Returns approval URL to front-end

POST /api/payments/capture

Captures order after buyer approval

Updates DB and triggers reconciliation
