# 🔧 Eureka Service Registry

This module enables **service discovery** using **Spring Cloud Netflix Eureka**, allowing microservices within your system to register themselves and discover others dynamically — without needing hardcoded URLs.

---

## 📦 Purpose

- Acts as a **central registry** where all microservices can register at startup.
- Enables **dynamic discovery** between services, promoting scalability and decoupling.
- Avoids the need to configure fixed endpoints or expensive cloud load balancers for internal communication.

---

## 🧭 Internal Service Communication Strategies

There are two common ways microservices can communicate:

### 1. Using AWS Load Balancer (LB)
- Works well if you have a **small number of services**.
- Easy to set up and manage.
- Ideal when **clients can afford cloud LB costs**.

### 2. Using Eureka Service Registry
- Better for **larger or growing systems**.
- No need to manually configure routing rules.
- **Automatically handles load balancing** and **service discovery**.
- More cost-effective and optimized for Spring Boot apps.

📌 **In this project**, we use **Eureka Service Registry** for inter-service communication.

---

## 🧱 Architecture Overview

```text
[payment-processor-service] ────────┐
                                   │
                                   ▼
                        [Eureka Service Registry]
                                   ▲
                                   │
         [paypal-provider-service] ───────► Lookup & Call

