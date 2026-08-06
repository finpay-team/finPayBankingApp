-- 1. Ensure the schema exists
CREATE SCHEMA IF NOT EXISTS finpay;

-- 2. Create the accounts table
CREATE TABLE finpay.accounts (
                                 id UUID PRIMARY KEY,
                                 account_number VARCHAR(20) NOT NULL UNIQUE,
                                 account_name VARCHAR(100) NOT NULL,
                                 account_type VARCHAR(30) NOT NULL,
                                 currency VARCHAR(10) NOT NULL,
                                 balance NUMERIC(19, 4) NOT NULL DEFAULT 0,
                                 available_balance NUMERIC(19, 4) NOT NULL DEFAULT 0,
                                 account_status VARCHAR(20) NOT NULL,
                                 branch_code VARCHAR(20),
                                 ifsc_code VARCHAR(20),
                                 daily_transfer_limit NUMERIC(19, 4),
                                 monthly_transfer_limit NUMERIC(19, 4),
                                 version BIGINT DEFAULT 0,
                                 user_id UUID NOT NULL,
                                 opened_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                 closed_at TIMESTAMP WITH TIME ZONE,
                                 created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                 updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,

    -- Link this account directly to your existing users table
                                 CONSTRAINT fk_account_user
                                     FOREIGN KEY (user_id)
                                         REFERENCES auth.users (usr_uid)
                                         ON DELETE CASCADE
);