-- File needed for flyway to mangage tables in db

CREATE SEQUENCE IF NOT EXISTS transaction_id_seq AS BIGINT START 1 INCREMENT 1;

CREATE TABLE IF NOT EXISTS transaction
(
    id BIGINT PRIMARY KEY NOT NULL DEFAULT nextval('transaction_id_seq'),
    client_id BIGINT NOT NULL REFERENCES client(id),
    total_price NUMERIC(10,2) NOT NULL,
    completed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    event_id BIGINT NOT NULL REFERENCES event(id)
);

CREATE SEQUENCE IF NOT EXISTS transaction_component_id_seq AS BIGINT START 1 INCREMENT 1;

CREATE TABLE IF NOT EXISTS transaction_component
(
    id BIGINT PRIMARY KEY NOT NULL DEFAULT nextval('transaction_component_id_seq'),
    transaction_id BIGINT NOT NULL REFERENCES transaction(id)
-- add comma, and delete comments
--     ticket_id BIGINT NOT NULL REFERENCES ticket(id)
);

