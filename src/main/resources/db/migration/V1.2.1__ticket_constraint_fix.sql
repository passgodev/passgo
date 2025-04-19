ALTER TABLE "ticket" DROP CONSTRAINT if exists "ticket_seat_id_key";
ALTER TABLE "ticket" DROP CONSTRAINT if exists "ticket_client_id_key";

ALTER TABLE "transaction" DROP COLUMN if exists "event_id";