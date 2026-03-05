-- Users table
CREATE TABLE IF NOT EXISTS users (
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(50)  NOT NULL,
    email       VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    created_at  TIMESTAMP
);

-- Accounts table
CREATE TABLE IF NOT EXISTS accounts (
    id         BIGSERIAL PRIMARY KEY,
    user_id    BIGINT NOT NULL REFERENCES users(id),
    name       VARCHAR(100) NOT NULL,
    type       VARCHAR(20)  NOT NULL CHECK (type IN ('CHECKING','SAVINGS')),
    balance    NUMERIC(19,4) NOT NULL DEFAULT 0,
    created_at TIMESTAMP
);

-- Transactions table
CREATE TABLE IF NOT EXISTS transactions (
    id         BIGSERIAL PRIMARY KEY,
    account_id BIGINT NOT NULL REFERENCES accounts(id),
    amount     NUMERIC(19,4) NOT NULL,
    type       VARCHAR(20)  NOT NULL CHECK (type IN ('INCOME','EXPENSE')),
    category   VARCHAR(50)  NOT NULL,
    date       DATE         NOT NULL,
    note       VARCHAR(255),
    created_at TIMESTAMP
);

-- Budgets table
CREATE TABLE IF NOT EXISTS budgets (
    id            BIGSERIAL PRIMARY KEY,
    user_id       BIGINT NOT NULL REFERENCES users(id),
    category      VARCHAR(50)  NOT NULL,
    monthly_limit NUMERIC(19,4) NOT NULL,
    created_at    TIMESTAMP
);