-- what to do with time zone?? in 'expires_at'?

CREATE SEQUENCE "refresh_token_id_seq" as bigint start 1 increment 1;

CREATE TABLE "refresh_token"
(
    id bigint primary key not null default nextval('refresh_token_id_seq'),
    token uuid not null,
    member_credential_id bigint not null references member_credential(id),
    expires_at timestamp not null
);

ALTER SEQUENCE "refresh_token_id_seq" OWNED BY refresh_token.id;