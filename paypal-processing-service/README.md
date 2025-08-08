# 🧾 PayPal Processing Service

The `paypal-processing-service` is a core microservice in our payment infrastructure. It ensures accurate transaction state management after a user initiates a payment using PayPal. It performs **background reconciliation**, **order status checks**, and **auto-captures payments** when required — ensuring all PayPal transactions reach a final state of either `SUCCESS` or `FAILURE`.

To improve fault tolerance and maintain system resilience, we have implemented a **Circuit Breaker** using the @CircuitBreaker annotation from the Resilience4j library. This helps gracefully handle failures in downstream services (e.g., PayPal APIs or database operations), and fallback logic ensures that partial failures do not cascade across the system.

---

## 🌐 Purpose

- Processes transactions left in `PROCESSING` state.
- Handles cases where payment was approved by the customer, but the final **"capture"** step failed due to network issues or server crashes.
    - 📌 *Capture* is a backend API call that actually deducts money from the customer’s account after they approve the payment.
    - If this step fails (e.g., due to timeouts or system errors), the transaction stays in an incomplete state.
- Maintains **data consistency** between PayPal and our internal DB.
- Ensures that all payments eventually reach a final state using **scheduled retries** and **fallback logic**.

---

## 💳 PayPal Checkout Flow (Standard)

We are using **PayPal Standard Checkout** integration.

### 1. User Chooses PayPal Payment Option

- User selects PayPal as the payment method during checkout.

### 2. Call `createOrder` API

- Our `paypal-provider-service` sends an order creation request to PayPal using the `createOrder` API.
- A PayPal-hosted payment page URL is returned.
- **Initial PayPal order status** is: `PAYER_ACTION_REQUIRED`

### 3. Customer Completes Payment on PayPal

- Customer logs in and authorizes the payment on PayPal.
- **Order status becomes `APPROVED`**
  - ⚠️ **Money is not yet deducted from the customer.**

### 4. Call `captureOrder` API (Backend)

- We call `captureOrder` (from our backend) to finalize the payment.
- If successful:
  - PayPal changes the order status to `COMPLETED`
  - We mark the transaction as `SUCCESS` in our DB.

---

## 🔁 Why Reconciliation Is Needed

Sometimes the ideal flow breaks due to:

- Customer closes the PayPal tab or abandons the payment
- Network delay or timeout between `APPROVED` and `CAPTURE` API
- PayPal returns temporary errors
- Backend service crash or network error

In such cases, the transaction remains in `PROCESSING` state in our DB.

We use a **reconciliation scheduler** to recover from this.

---

## 🔄 Reconciliation Logic

A scheduled job runs every 10 minutes to identify and resolve transactions stuck in the `PROCESSING` state.

### Steps Involved:

1. **Fetch Processing Transactions**  
   - Select all transactions from the database where status = `PROCESSING`.

2. **Call PayPal `getOrder` API**  
   - Retrieve the latest status of the PayPal order from PayPal.

3. **Handle Based on PayPal Order Status:**

   - **PAYER_ACTION_REQUIRED**  
     - This means the customer has not yet completed the payment.  
     - We **do not take any action** other than logging and monitoring.
     - We continue checking every 10 minutes.  
     - If after **3 attempts** (i.e., 30 minutes) the customer still hasn't completed the payment, we mark the transaction as **`FAILED`**.

   - **APPROVED**  
     - The customer has approved the payment, but it hasn't been captured yet.  
     - We attempt to **capture** the payment using the `captureOrder` API.  
     - If capture is successful, we mark the transaction as **`SUCCESS`**.  
     - If capture fails, we retry (up to 3 attempts in total).

   - **COMPLETED**  
     - The payment has already been successfully captured.  
     - We mark the transaction as **`SUCCESS`** in our database.

   - **Any Other Status**  
     - For unexpected or error states, we log the issue and skip the transaction for now to avoid incorrect processing.

4. **Max Retry Check**  
   - If a transaction has been retried **3 times** without reaching a final state, it is marked as **`FAILED`** to prevent it from remaining in `PROCESSING` indefinitely

---

## 🧾 Internal Transaction Statuses (Our System)

| Status     | Meaning                                          |
|------------|--------------------------------------------------|
| SUCCESS    | Payment completed and captured successfully      |
| PROCESSING | Awaiting payment action or capture               |
| FAILED     | Payment attempt expired, abandoned, or errored   |

---

## 📦 PayPal Order Statuses (External)

| PayPal Status         | Meaning                                      |
|-----------------------|----------------------------------------------|
| PAYER_ACTION_REQUIRED | Waiting for customer to complete payment     |
| APPROVED              | Customer approved, but capture not done yet |
| COMPLETED             | Payment captured, money deducted             |

---

## ⏱ Retry Strategy

The system attempts to process each transaction **every 10 minutes** within a **30-minute window**.

This provides the customer with a **30-minute timeframe** to complete the payment.

If the transaction is not successfully captured after **3 attempts** (i.e., after 30 minutes), it is marked as **`FAILED`**.

This strategy ensures **timely payment processing** and prevents **indefinite pending states**.

---

## 🧯 Circuit Breaker Integration

To improve system resilience and avoid cascading failures, we have integrated a **Circuit Breaker** using **Resilience4j**.

### 💡 Why?

When the `paypal-provider-service` is slow or unresponsive:
- Circuit Breaker avoids repeatedly failing calls,
- Instead, it triggers a **fallback method** immediately,
- This allows the system to degrade gracefully and gives time for recovery.

## 🧠 Summary

The `paypal-processing-service` ensures reliability in our PayPal integration by handling delayed or incomplete payments through scheduled reconciliation. It makes sure no transaction is left unresolved — leading to better consistency, user experience, and system integrity.

---

## 📦 Package Structure - paypal-processing-service

### com.hulkhire.payments.config
Contains configuration classes such as schedulers, circuit breakers, and other service-level configurations.

### com.hulkhire.payments.constants
Holds application-wide constant values to avoid hardcoded literals.

### com.hulkhire.payments.controller
REST controllers that expose APIs related to payment processing operations.

### com.hulkhire.payments.dao.impl
DAO implementation classes responsible for interacting with the database.

### com.hulkhire.payments.dao.interfaces
DAO interfaces defining contracts for data access operations.

### com.hulkhire.payments.DTO
Data Transfer Objects used for communication between layers.

### com.hulkhire.payments.entity
Contains JPA entity classes representing database tables.

### com.hulkhire.payments.exceptions
Custom exception classes and global exception handlers.

### com.hulkhire.payments.http
Handles HTTP-related utilities or wrappers used for external communication.

### com.hulkhire.payments.paypal.res
Represents response models specific to PayPal API responses.

### com.hulkhire.payments.pojo
General-purpose POJOs used across different layers of the service.

### com.hulkhire.payments.service
Contains core service logic and high-level orchestration.

### com.hulkhire.payments.service.Helper
Helper classes that support reusable service logic.

### com.hulkhire.payments.service.impl
Concrete implementations of service interfaces.

### com.hulkhire.payments.service.interfaces
Interfaces for the business logic layer, promoting loose coupling.

### com.hulkhire.payments.utils
Utility classes with static helper methods used throughout the service.

### com.hulkhire.payments.utils.converters
Handles conversion between DTOs and Entities for mapping purposes.


## 📌 Related Links

- [PayPal Standard Checkout Documentation](https://developer.paypal.com/studio/checkout/standard/getstarted)

---

