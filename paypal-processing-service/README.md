# 🧾 PayPal Processing Service

The `paypal-processing-service` is a core microservice in our payment infrastructure. It ensures accurate transaction state management after a user initiates a payment using PayPal. It performs **background reconciliation**, **order status checks**, and **auto-captures payments** when required — ensuring all PayPal transactions reach a final state of either `SUCCESS` or `FAILURE`.

---

## 🌐 Purpose

- Processes transactions left in `PROCESSING` state.
- Recovers from missed capture calls due to network delays or unexpected errors.
- Maintains **data consistency** between PayPal and our internal DB.
- Ensures that payments are finalized reliably with **retries**, **delays**, and **fallbacks**.

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

A scheduled job runs every few minutes to identify and resolve transactions in `PROCESSING` status.

### Steps Involved:

1. **Fetch Processing Transactions**  
   - From our database where status = `PROCESSING`.

2. **Call PayPal `getOrder` API**  
   - Retrieve the latest status of the PayPal order.

3. **Handle Based on Order Status:**

   - **PAYER_ACTION_REQUIRED**  
     The customer hasn't completed the payment.  
     We wait for a maximum of 30 minutes, retrying up to 3 times. If still not completed, mark as `FAILED`.

   - **APPROVED**  
     The customer has approved the payment, but it hasn’t been captured.  
     We call the `captureOrder` API. If successful, mark the transaction as `SUCCESS`.

   - **COMPLETED**  
     Payment was already captured (possibly by another process).  
     We mark the transaction as `SUCCESS`.

   - **Any Other Status**  
     Unexpected or error state — we log and skip for further investigation.

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

- Each processing transaction is retried up to **3 times** over a **30-minute window**.
- If still not completed, it is marked as `FAILED`.
- Helps avoid stuck or indefinite transactions.

---

## 🧠 Summary

The `paypal-processing-service` ensures reliability in our PayPal integration by handling delayed or incomplete payments through scheduled reconciliation. It makes sure no transaction is left unresolved — leading to better consistency, user experience, and system integrity.

---

## 📌 Related Links

- [PayPal Standard Checkout Documentation](https://developer.paypal.com/studio/checkout/standard/getstarted)

---

## 👨‍💻 Author

**Siddabathula Pavan Kumar**  
Java Full Stack Developer  
[LinkedIn](https://www.linkedin.com/in/siddabathula-pavan-kumar/)
