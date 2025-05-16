ALTER TABLE transaction
    ALTER COLUMN total_price TYPE NUMERIC(10, 2);


ALTER TABLE transaction
    ADD COLUMN transaction_type VARCHAR(255) NOT NULL;