# Customer Transaction Reward Points Calculator

## Project Overview
This project is a RESTful Web API built using **Spring Boot** that calculates reward points for a retailer's customerTransactions based on their transaction history. The system processes transaction data over a three-month period to determine points earned per month and the cumulative total for each customer.

## Reward Logic
The application follows a specific scoring algorithm for points awarded per transaction:
* **2 points** for every dollar spent over **$100**.
* **1 point** for every dollar spent between **$50 and $100**.
* *Example:* A **$120** purchase calculates as:
  * $(2 times \$20 [amount over \$100]) + (1 times \$50  [amount between \$50 and \$100]) = 90 points\$

## Technical Stack
* **Java 21**
* **Spring Boot 3.5.40**
* **Spring Data JPA** (H2 In Memory Based Database )
* **Maven** (Project Management)
* **JUnit 5 & Mockito** (Testing)

## Database Initialization
The project is configured to automatically populate the H2 in-memory database upon startup. The following scripts are located in src/main/resources:

* **customerTransaction-table.sql: Defines the database schema and table structure**
* **customerTransaction-data.sql: Populates the tables with initial customer and transaction records for testing**

## Key Features & Requirements Met
* **RESTful Endpoints:** Provides scalable APIs to fetch reward details dynamically based on customerId and timeframes.
* **Global Exception Handling:** Implemented via `CustomerTransactionControllerAdvice` to handle errors gracefully.
* **Input Validation:** Ensures all incoming data meets technical and business constraints.
* **Clean Architecture:** Follows a standard 3-tier architecture (Controller -> Service -> Repository) with DTOs for data transfer.
* **Automated Testing:** Includes comprehensive unit tests in `TransactionServiceTest` to verify reward logic across multiple scenarios.

## Getting Started

### Prerequisites
* JDK 21 or higher 
* Maven 3.6+

### Installation & Running
1.  **Clone the repository:**
    ```bash
    git clone https://github.com/YourUsername/CustomerRewardsDemo.git
    ```
2.  **Navigate to the project directory:**
    ```bash
    cd customerTransaction
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
**Endpoint:** `GET /getRewardsByCustomer/{customerId}`
**Description:** Fetches the monthly and total reward points for a specific customer with customerId over the given time frame.

Example Request
 * **GET: /getRewardsByCustomer/Baskin1234?startDate=01-04-2025 00:00:00&endDate=30-04-2026 23:59:59**

Example Response in Json Format
````json
{
    "customerName": "John Baskin",
    "customerTransactionId": "Baskin1234",
    "rewardPerMonth": {
        "APRIL-2025": 152.00,
        "JULY-2025": 152.00,
        "JANUARY-2026": 1.00,
        "MARCH-2026": 352.00
    },
    "totalReward": 657.00
}
````

### 🧪 Sample Data Payloads
Based on the customerTransaction-data.sql configuration, the following transactions represent the data for John Baskin (ID: Baskin1234) :

**Sample Data for John Baskin(CustomerId:Baskin1234) for which we are requesting to fetch rewards details** :
````json
{
  "customerId": "Baskin1234",
  "customerName": "John Baskin",
  "transactionId": "TXN1-947654",
  "transactionInDollar": 151.0,
  "transactionDateTime": "01-04-2025 22:30:46"
}
````
````json
{
  "customerId": "Baskin1234",
  "customerName": "John Baskin",
  "transactionId": "TXN-9476547",
  "transactionInDollar": 151.0,
  "transactionDateTime": "09-07-2025 19:30:15"
}
````
````json
{
  "customerId": "Baskin1234",
  "customerName": "John Baskin",
  "transactionId": "TXN1-9476547",
  "transactionInDollar": 51.0,
  "transactionDateTime": "03-01-2026 00:00:00"
}
````
````json
{
  "customerId": "Baskin1234",
  "customerName": "John Baskin",
  "transactionId": "TXN1-9476543",
  "transactionInDollar": 251.0,
  "transactionDateTime": "31-03-2026 23:59:59"
}

````

## Project Structure
```text
customerTransaction/
├── src/main/java/charterDemo/customerTransaction/
│   ├── controller/             # REST API Controllers
│   ├── DTO/                    # Data Transfer Objects
│   ├── entity/                 # Database Entity classes
│   ├── exception/              # Custom Business Exceptions
│   ├── globalExceptionHandler/ # Centralized Exception Handling Logic
│   ├── repository/             # JPA Repositories for Database access
│   ├── service/                # Business Logic layer
│   └── CustomerTransactionApplication.java
├── src/main/resources/
│   ├── static/                 # Static assets
│   ├── templates/              # View templates
│   ├── application.properties  # App configuration
│   ├── customerTransaction-data.sql
│   └── customerTransaction-table.sql
├── src/test/java/charterDemo/customerTransaction/
│   ├── controller/             # Controller layer unit tests
│   │   └── TransactionControllerTest.java
│   ├── service/                # Service layer unit tests
│   │   └── TransactionServiceTest.java
│   └── CustomerTransactionApplicationTests.java
├── pom.xml                     # Maven dependencies
└── README.md


