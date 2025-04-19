-- 2025.04.17
-- create member_credential relation
CREATE SEQUENCE IF NOT EXISTS "member_credential_id_seq" AS BIGINT START 1 INCREMENT 1;

CREATE TABLE IF NOT EXISTS "member_credential"
(
    id BIGINT PRIMARY KEY NOT NULL DEFAULT nextval('member_credential_id_seq'),
    login varchar(32) NOT NULL UNIQUE,
    password varchar(255) NOT NULL
);

ALTER TABLE "member_credential" OWNER TO "user";

-- alter existing relations to include foreign key attribute
ALTER TABLE "client"
    ADD COLUMN "member_credential_id" BIGINT NOT NULL DEFAULT NULL
    REFERENCES "member_credential";

ALTER TABLE "organizer"
    ADD COLUMN "member_credential_id" BIGINT NOT NULL DEFAULT NULL
    REFERENCES "member_credential";

ALTER TABLE "administrator"
    ADD COLUMN "member_credential_id" BIGINT NOT NULL DEFAULT NULL
    REFERENCES "member_credential";