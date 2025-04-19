-- 2025.04.17
-- delete wallet attribute named client_id, no way to instatiate both client and wallet at the same time
-- wallet is first created then, assignedto client
ALTER TABLE "wallet" DROP COLUMN "client_id";
ALTER TABLE "client" ADD FOREIGN KEY ("wallet_id") REFERENCES "wallet" ON DELETE CASCADE;