ALTER TABLE event
    ADD COLUMN organizer_id BIGINT NOT NULL;

ALTER TABLE event
    ADD CONSTRAINT fk_event_organizer
        FOREIGN KEY (organizer_id)
            REFERENCES organizer(id);