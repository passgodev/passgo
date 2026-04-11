ALTER TABLE transaction_component
    ADD CONSTRAINT fk_ticket_id FOREIGN KEY (ticket_id) REFERENCES ticket(id);