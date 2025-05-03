-- explicitly references id of member_credential
-- created in V1.1.1 below version was only for learning purposes, postgresql default fk name creation is:
-- <table_name>_<attribute_name>_fkey
-- ALTER TABLE "client"
--     DROP CONSTRAINT IF EXISTS "member_credential_member_credential_id_fkey",
--     ADD CONSTRAINT "member_credential_member_credential_id_fkey" FOREIGN KEY (member_credential_id) REFERENCES member_credential(id);
--
-- ALTER TABLE "organizer"
--     DROP CONSTRAINT IF EXISTS "member_credential_member_credential_id_fkey",
--     ADD CONSTRAINT "member_credential_member_credential_id_fkey" FOREIGN KEY (member_credential_id) REFERENCES member_credential(id);
--
-- ALTER TABLE "administrator"
--     DROP CONSTRAINT IF EXISTS "member_credential_member_credential_id_fkey",
--     ADD CONSTRAINT "member_credential_member_credential_id_fkey" FOREIGN KEY (member_credential_id) REFERENCES member_credential(id);

ALTER TABLE "member_credential" ADD COLUMN "is_active" BOOL DEFAULT FALSE;

-- move all is_active attribute values newly created column in member_credential table
UPDATE member_credential AS mc
SET is_active = cli.is_active
FROM client as cli
    WHERE mc.member_type LIKE 'CLIENT' AND mc.id = cli.member_credential_id;

UPDATE member_credential AS mc
SET is_active = org.is_active
FROM organizer as org
    WHERE mc.member_type LIKE 'ORGANIZER' AND mc.id = org.member_credential_id;

UPDATE member_credential AS mc
SET is_active = adm.is_active
FROM administrator as adm
    WHERE mc.member_type LIKE 'ADMINISTRATOR' AND mc.id = adm.member_credential_id;

-- delete moved columns
ALTER TABLE "client" DROP COLUMN "is_active";
ALTER TABLE "organizer" DROP COLUMN "is_active";
ALTER TABLE "administrator" DROP COLUMN "is_active";