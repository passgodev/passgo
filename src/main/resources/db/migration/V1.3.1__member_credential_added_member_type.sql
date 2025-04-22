ALTER TABLE "member_credential"
    ADD COLUMN "member_type"  varchar(16) DEFAULT 'CLIENT'
    CONSTRAINT "check_member_type" CHECK ( member_type IN ('CLIENT', 'ORGANIZER', 'ADMINISTRATOR') )