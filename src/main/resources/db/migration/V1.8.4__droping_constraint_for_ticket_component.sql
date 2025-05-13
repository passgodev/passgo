ALTER TABLE transaction_component
    DROP CONSTRAINT ukb2u25mtudqgk6cu5ewc7pg5e9;

ALTER TABLE transaction_component
    ADD CONSTRAINT fk_ticket_id FOREIGN KEY (ticket_id) REFERENCES ticket(id);