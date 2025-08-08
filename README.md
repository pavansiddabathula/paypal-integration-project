# 💳 PayPal Integration Project

## 🧾 Project Overview

This project is a secure and scalable payment system developed for a client-facing website, where users can complete purchases using PayPal Standard Checkout. Customers can choose PayPal as a payment option during checkout and complete their transaction smoothly.

---

## 🧱 Microservice Architecture

It follows a **Microservices Architecture** 
This project consists of the following **three microservices**, organized as separate folders in this repository:

1. **paypal-provider-service** – Handles PayPal API calls (Create Order, Capture Order, Get Order).
2. **paypal-processing-service** – Manages payment flow logic, reconciliation, and database status updates.
3. **eureka-service-registry** – Registers all services for service discovery.

> 📄 **Each service has its own `README.md` file** with detailed setup and implementation instructions.  
> Browse the respective folder for complete documentation.

---

# 💳 PayPal Checkout – Success Flow

This section explains how the PayPal Standard Checkout flow works in a successful payment scenario, written in a simple way for anyone to understand — even without technical background.

## ✅ Successful Payment Journey

1. **Customer clicks “Pay with PayPal”**  
   The customer selects a product/service and clicks the “Pay with PayPal” button on the website.

2. **Order is created on PayPal**  
   The system sends a secure request to PayPal to create a new order for the selected product or service.

3. **Customer logs in and approves payment**  
   PayPal opens in a popup or new window. The customer logs into their PayPal account and approves the payment.

4. **System captures the payment**  
   Once the payment is approved by the customer, the system immediately sends a request to PayPal to **capture** the payment.  
   > ⚠️ *This step is important: capturing the payment confirms and completes the transaction.*

5. **Capture is successful**  
   PayPal confirms that the payment has been successfully captured.

6. **System updates payment status**  
   The system marks the transaction as `SUCCESS` in the database.

7. **Customer sees confirmation**  
   A success message is shown to the customer — for example:  
   > 💬 “Payment Complete!”

   The entire success flow is handled by the paypal-provider-service
   To know more about the paypal-provider-service, please refer to paypal-integration-project/paypal-provider-service/README.md for detailed information.

---

## 🛑 Failed or Incomplete or Is Still in Process?

Sometimes, the payment might not go through immediately. This can happen due to various reasons — the customer closes the window, network issues, PayPal delay, or the capture step fails. Here's how we handle that:

### 🔁 Step-by-Step Flow (Failure or Processing Scenario)

1. The customer clicks **"Pay with PayPal"** and approves the payment.

2. When our system tries to **capture** the payment:
   - If PayPal doesn't confirm the payment (due to failure or delay),
   - We **do not mark it as "successful"** in our database right away.

3. Instead, the payment is stored as **"Processing"** or **"Pending"** in our database.

---

## 🛠️ Role of `paypal-processing-service` (Recon System)

We built a second microservice called **`paypal-processing-service`**, which acts like a watchdog to track and update these incomplete payments.

Here's how it works:

- Every few minutes, this service **automatically checks the database** for any payments that are still in "Processing" or "Pending" status.
  
- For each such payment:
  - It sends a fresh request to PayPal to **check the current status**.
  - If the payment is now **successful**, it updates our database and marks it as "Completed".
  - If it's still **not successful**, the service will keep checking in future cycles.
  - If the payment **definitely failed**, it marks the payment as "Failed" and logs the reason.

---

## ✅ Why This Is Important

This setup ensures that:
- No payment gets missed or forgotten.
- Customers aren't wrongly shown as unpaid.
- The system is reliable, even if PayPal takes time or something goes wrong during the initial step.
For additional insights into the paypal-processing-service, check out paypal-integration-project/paypal-processing-service/README.md.

---
## 🖼️ Flow Diagram

![PayPal Checkout Flow](assets/Screenshot%202025-08-06%20175421.png)

---

## ⚙️ Tech Stack

| Category         | Technologies Used                          |
|------------------|--------------------------------------------|
| Backend          | Java, Spring Boot, Spring MVC              |
| Microservices    | Eureka, Feign Client, RestTemplate         |
| Database         | MySQL                                      |
| Cache            | Redis (for access tokens & job scheduling) |
| Cloud            | AWS EC2, RDS, Secrets Manager              |
| Security         | OAuth 2.0 (Client Credentials Flow)        |
| Dev Tools        | Maven, Git, GitHub                         |
| API Testing      | Postman Collections                        |
| Testing          | JUnit, Mockito                             |

---
## 📁 Repository Structure

paypal-integration-project/

paypal-provider-service – PayPal API integration service (create, get, capture orders)

paypal-processing-service – Core logic for payment verification and reconciliation (scheduled job-based)

eureka-service-registry – Service registry to manage microservices

assets – Contains architecture diagrams, flowcharts, and screenshots

README.md – Project overview and usage instructions
