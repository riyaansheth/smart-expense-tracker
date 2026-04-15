CREATE DATABASE IF NOT EXISTS expense_tracker;
USE expense_tracker;

CREATE TABLE IF NOT EXISTS users (
    id   INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL
);

INSERT INTO users (name)
SELECT 'Default User'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE name = 'Default User');

CREATE TABLE IF NOT EXISTS transactions (
    id       INT AUTO_INCREMENT PRIMARY KEY,
    type     VARCHAR(10)  NOT NULL,
    amount   DOUBLE       NOT NULL,
    category VARCHAR(100) NOT NULL,
    date     DATE         NOT NULL,
    user_id  INT,
    FOREIGN KEY (user_id) REFERENCES users(id)
        ON DELETE SET NULL
        ON UPDATE CASCADE
);
