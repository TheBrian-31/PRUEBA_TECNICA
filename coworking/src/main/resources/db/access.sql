CREATE SCHEMA IF NOT EXISTS access;

CREATE TABLE IF NOT EXISTS access.role (
    id          SMALLSERIAL PRIMARY KEY,
    code        VARCHAR(20)  NOT NULL,
    description VARCHAR(120) NOT NULL,
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW(),

    CONSTRAINT uq_role_code UNIQUE (code),
    CONSTRAINT ck_role_code CHECK (code IN ('ADMIN', 'USER'))
);

CREATE TABLE IF NOT EXISTS access.account (
    id            BIGSERIAL    PRIMARY KEY,
    full_name     VARCHAR(120) NOT NULL,
    email         VARCHAR(255) NOT NULL,
    password_hash VARCHAR(72)  NOT NULL,
    role_id       SMALLINT     NOT NULL,
    enabled       BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at    TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMPTZ  NOT NULL DEFAULT NOW(),

    CONSTRAINT uq_account_email UNIQUE (email),
    CONSTRAINT ck_account_email_lowercase CHECK (email = LOWER(email)),
    CONSTRAINT ck_account_full_name_not_blank CHECK (BTRIM(full_name) <> ''),
    CONSTRAINT fk_account_role
        FOREIGN KEY (role_id) REFERENCES access.role (id)
);

CREATE INDEX IF NOT EXISTS idx_account_role_id ON access.account (role_id);

INSERT INTO access.role (code, description)
VALUES
    ('ADMIN', 'Administra espacios y todas las reservas'),
    ('USER',  'Consulta espacios y gestiona sus propias reservas')
ON CONFLICT (code) DO NOTHING;
