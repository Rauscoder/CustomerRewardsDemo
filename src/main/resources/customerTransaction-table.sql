CREATE TABLE customer_transaction (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id VARCHAR(255) NOT NULL,
    customer_name VARCHAR(255) NOT NULL,
    transaction_id VARCHAR(255) NOT NULL UNIQUE,
    transaction_in_dollar DECIMAL(19, 5) NOT NULL,
    transaction_date_time TIMESTAMP NOT NULL
);