ALTER TABLE transaction_component
    DROP CONSTRAINT IF EXISTS transaction_component_ticket_id_fkey;

ALTER TABLE transaction_component
    ADD CONSTRAINT fk_ticket_id FOREIGN KEY (ticket_id) REFERENCES ticket(id);