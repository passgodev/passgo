CREATE TABLE event_organizer (
    id             BIGINT      GENERATED ALWAYS AS IDENTITY,
    organizer_id   BIGINT      NOT NULL,
    organizer_type VARCHAR(50) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uq_event_organizer UNIQUE (organizer_id, organizer_type)
);