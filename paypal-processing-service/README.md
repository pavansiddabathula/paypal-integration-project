# PayPal Provider Service

This microservice is responsible for integrating PayPal Standard Checkout into our overall payment system. It acts as a Payment Service Provider (PSP) module and handles all core PayPal-related operations such as order creation, capturing payments, and retrieving order status.

---

## 📌 Features

- Integration with PayPal Standard Checkout
- Implements key PayPal REST APIs:
  - `createOrder`
  - `getOrder`
  - `captureOrder`
- OAuth 2.0-based authentication
- Redis caching for token management
- Designed for scale, reliability, and fault tolerance

---

## 🔁 Standard Checkout Flow

1. **Customer initiates a purchase** on the e-commerce platform.
2. The platform triggers the `createOrder` API to initiate a PayPal transaction.
3. PayPal presents a hosted checkout page to the customer.
4. The customer logs in and authorizes payment.
5. The platform can fetch payment status using the `getOrder` API.
6. Once authorized, the `captureOrder` API is triggered to finalize and deduct the payment.
7. Payment is settled to the merchant account after deducting PayPal fees.

🔗 [PayPal Standard Checkout Docs](https://developer.paypal.com/studio/checkout/standard/getstarted)

---

## ⚙️ API Endpoints

### 1. `POST /api/paypal/createOrder`
Creates a new PayPal order and returns a redirect link to the hosted PayPal page.

**Example Request:**  
POST  
http://localhost:8081/api/paypal/createOrder

---

### 2. `GET /api/paypal/getOrder/{orderId}`
Retrieves the current status and details of a given PayPal order.

**Example Request:**  
GET  
http://localhost:8081/api/paypal/getOrder/5MN12345GH909123J

---

### 3. `POST /api/paypal/captureOrder/{orderId}`
Captures the authorized payment from the customer's PayPal account.

**Example Request:**  
POST  
http://localhost:8081/api/paypal/captureOrder/5MN12345GH909123J

---


## 🔐 OAuth 2.0 Authentication

- We authenticate using PayPal’s OAuth 2.0 token endpoint.
- The access token is valid for 8 hours.
- We cache the token in Redis with a TTL of **7 hours 30 minutes**.
- Once expired, the system automatically re-fetches and stores a new token.
- This ensures seamless, uninterrupted access to PayPal APIs.

---

## 🧠 Technical Details

- Language: Java
- Architecture: Microservices
- External Integration: PayPal REST APIs
- Token Store: Redis
- HTTP Client: WebClient / RestTemplate (based on implementation)
- Rate Limiting / Error Handling: Implemented at API level

---

## 🧾 Reference Links

- [Create Order API](https://developer.paypal.com/docs/api/orders/v2/#orders_create)
- [Capture Order API](https://developer.paypal.com/docs/api/orders/v2/#orders_capture)
- [PayPal REST APIs Overview](https://developer.paypal.com/api/rest/)
- [PayPal Sandbox Login](https://sandbox.paypal.com)
- [PayPal Developer Dashboard](https://developer.paypal.com/dashboard/)

---

## 🧮 Payment System Context

This service is part of a larger payment ecosystem, designed to support:

- Multiple Payment Methods: Cards, UPI, Wallets, Net Banking, BNPL
- Payment Types: One-time (SALE), Subscription (RECC)
- Hosted Page and Direct Integration
- Fault-tolerant transaction management

---

## 📌 Why PayPal?

PayPal is a globally trusted Payment Service Provider offering:

- High security and fraud protection
- Seamless global transactions
- Multi-currency support
- Easy integration with modern APIs

---

## 📦 Related Services

- `paypal-processing-service`: Handles reconciliation and payment verification logic
- `order-service`: Manages customer orders
- `auth-service`: Centralized authentication across services

---

## 🧪 Test it

Use the official [PayPal Sandbox](https://sandbox.paypal.com) to simulate payments and test different flows.

---



