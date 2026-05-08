# Customer Transaction Reward Points Calculator

## Project Overview
This project is a RESTful Web API built using **Spring Boot** that calculates reward points for a retailer's customerTransactions based on their transaction history. The system processes transaction data over a three-month period to determine points earned per month and the cumulative total for each customer.

## Reward Logic
The application follows a specific scoring algorithm for points awarded per transaction:
* **2 points** for every dollar spent over **$100**.
* **1 point** for every dollar spent between **$50 and $100**.
* *Example:* A **$120** purchase calculates as:
    * $(2 \times \$20 \text{ [amount over \$100]}) + (1 \times \$50 \text{ [amount between \$50 and \$100]}) = \mathbf{90 \text{ points}}$.

## Technical Stack
* **Java 8/17**
* **Spring Boot 3.x**
* **Spring Data JPA** (H2 File Based Database for persistant storage)
* **Maven** (Project Management)
* **JUnit 5 & Mockito** (Testing)

## Key Features & Requirements Met
* **RESTful Endpoints:** Provides scalable APIs to fetch reward details dynamically based on customer and timeframes.
* **Global Exception Handling:** Implemented via `CustomerTransactionControllerAdvice` to handle errors gracefully.
* **Input Validation:** Ensures all incoming data meets technical and business constraints.
* **Clean Architecture:** Follows a standard 3-tier architecture (Controller -> Service -> Repository) with DTOs for data transfer.
* **Automated Testing:** Includes comprehensive unit tests in `CustomerTransactionServiceTest` to verify reward logic across multiple scenarios.

## Getting Started

### Prerequisites
* JDK 8 or higher (JDK 21 recommended)
* Maven 3.6+

### Installation & Running
1.  **Clone the repository:**
    ```bash
    git clone [https://github.com/YourUsername/CustomerRewardsDemo.git](https://github.com/YourUsername/CustomerRewardsDemo.git)
    ```
2.  **Navigate to the project directory:**
    ```bash
    cd CustomerTransaction
    ```
3.  **Build the project:**
    ```bash
    ./mvnw clean install
    ```
4.  **Run the application:**
    ```bash
    ./mvnw spring-boot:run
    ```
5. **Run the automated test and view result:**
     ```bash
    ./mvnw test
    ```
## API Usage

### 1. Calculate Rewards for a Customer
**Endpoint:** `GET /api/rewards/{customerId}`
**Description:** Fetches the monthly and total reward points for a specific customer over the last 3 months.

## Project Structure
```text
src/main/java/CharterDemo/CustomerTransaction/
├── controller/             # REST Controller for handling API requests
├── service/                # Business logic for points calculation
├── repository/             # JPA Repository for H2 database access
├── entity/                 # Database entities (Customer, Transactions)
├── DTO/                    # Data Transfer Objects for API responses
├── GlobalExceptionHandler/ # Centralized error handling (CustomerTransactionControllerAdvice)
└── util/                   # Custom exceptions and utilities

## 🧪 Sample Test Data (Postman)
You can use the following JSON payloads to populate your database via your POST endpoint:

### Customer: John Doe
```json
{
    "name": "John Doe",
    "transactionId": "TXN-847675",
    "transactionInDollar": 751,
    "transactionDateAndTime": "24-03-2025 20:30:15"
}

{
    "name": "John Doe",
    "transactionId": "TXN-947675",
    "transactionInDollar": 51,
    "transactionDateAndTime": "24-03-2026 10:30:15"
}

{
    "name": "John Doe",
    "transactionId": "TXN-947655",
    "transactionInDollar": 351,
    "transactionDateAndTime": "25-03-2026 12:30:15"
}

### Customer: John Baskin
```json
{
    "name": "John Baskin",
    "transactionId": "TXN-947654",
    "transactionInDollar": 351,
    "transactionDateAndTime": "31-04-2026 22:30:46"
}

{
    "name": "John Baskin",
    "transactionId": "TXN-9476547",
    "transactionInDollar": 51,
    "transactionDateAndTime": "03-02-2026 10:30:15"
}

{
    "name": "John Baskin",
    "transactionId": "TXN-9476547",
    "transactionInDollar": 51,
    "transactionDateAndTime": "03-02-2026 10:30:15"
}

{
    "name": "John Baskin",
    "transactionId": "TXN-9476547",
    "transactionInDollar": 257,
    "transactionDateAndTime": "09-02-2026 19:30:15"
}