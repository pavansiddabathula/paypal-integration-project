# PayPal Integration Project

## Project Overview
This project implements a **Payment System** for an e-commerce-like client application, enabling customers to pay securely using **PayPal Standard Checkout**.  
It follows a **Microservices Architecture** with three services:

1. **paypal-provider-service** – Handles PayPal API calls (Create Order, Capture Order, Get Order).
2. **paypal-processing-service** – Manages payment flow logic, reconciliation, and database status updates.
3. **eureka-service-registry** – Registers all services for service discovery.

The system ensures **secure, fault-tolerant, and scalable** payment processing with reconciliation logic and a **30-minute pending payment window**.

---
## Description

This project demonstrates how to integrate PayPal's payment processing capabilities into a Java-based microservices architecture. It includes services for handling payment processing, order creation, and reconciliation with PayPal's APIs.

## Tech Stack

- **Backend**: Java, Spring Boot, Spring MVC
- **Microservices**: 
  - Eureka Service Registry
  - Feign Client
  - RestTemplate
- **Database**: MySQL
- **Cache**: Redis
- **API Documentation**: Postman Collections
- **Cloud**: AWS EC2, RDS, Secrets Manager
- **Security**: OAuth 2.0 (Client Credentials)
- **Build Tool**: Maven
- **Version Control**: Git, GitHub
- **Testing**: JUnit, Mockito

## Microservices Overview

- **paypal-provider-service**: Integrates with PayPal APIs to manage orders.
- **paypal-processing-service**: Handles payment processing and reconciliation.
- **eureka-service-registry**: Manages service discovery for microservices.

## Standard PayPal Checkout Flow

1. Buyer clicks "Pay with PayPal" on the client application.
2. Front-end calls `paypal-processing-service` to initiate payment.
3. Processing service requests order creation from `paypal-provider-service`.
4. Provider service calls PayPal Orders API (Create Order).
5. Buyer logs in to PayPal and approves the payment.
6. Processing service calls provider service to capture payment.
7. Provider service calls PayPal Orders API (Capture Order).
8. Status is updated in the database (success/failure).
9. If not completed in 30 minutes, the system marks payment as failed.

## API Flow Example

### Create Payment

- **Endpoint**: `POST /api/payments/create`
- **Description**: Calls PayPal API to create an order and returns the approval URL to the front-end.

### Capture Payment

- **Endpoint**: `POST /api/payments/capture`
- **Description**: Captures the order after buyer approval, updates the database, and triggers reconciliation.

## Installation Instructions

1. Clone the repository:
   ```bash
   git clone https://github.com/pavansiddabathula/paypal-integration-project
