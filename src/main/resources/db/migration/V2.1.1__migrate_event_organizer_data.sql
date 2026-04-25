-- Populate event_organizer from existing organizers referenced in event
INSERT INTO event_organizer (organizer_id, organizer_type)
SELECT DISTINCT organizer_id, 'ORGANIZER'
FROM event
WHERE organizer_id IS NOT NULL;

-- Add event_organizer_id FK column to event
ALTER TABLE event
    ADD COLUMN event_organizer_id BIGINT;

-- Link each event to its new event_organizer row
UPDATE event e
SET event_organizer_id = eo.id
FROM event_organizer eo
WHERE eo.organizer_id = e.organizer_id
  AND eo.organizer_type = 'ORGANIZER';

-- Add FK constraint
ALTER TABLE event
    ADD CONSTRAINT fk_event_event_organizer
        FOREIGN KEY (event_organizer_id) REFERENCES event_organizer (id);

-- Remove old organizer FK and column
ALTER TABLE event DROP CONSTRAINT IF EXISTS fk_event_organizer;
ALTER TABLE event DROP COLUMN IF EXISTS organizer_id;