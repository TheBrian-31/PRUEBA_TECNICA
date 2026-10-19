CREATE SCHEMA IF NOT EXISTS booking;

CREATE TABLE IF NOT EXISTS booking.space (
    id             BIGSERIAL      PRIMARY KEY,
    name           VARCHAR(120)   NOT NULL,
    type           VARCHAR(30)    NOT NULL,
    capacity       INTEGER        NOT NULL,
    location       VARCHAR(120)   NOT NULL,
    hourly_rate    NUMERIC(10, 2) NOT NULL,
    out_of_service BOOLEAN        NOT NULL DEFAULT FALSE,
    created_at     TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    updated_at     TIMESTAMPTZ    NOT NULL DEFAULT NOW(),

    CONSTRAINT ck_space_name_not_blank CHECK (BTRIM(name) <> ''),
    CONSTRAINT ck_space_type CHECK (type IN ('WORK', 'RECREATIONAL')),
    CONSTRAINT ck_space_capacity_positive CHECK (capacity > 0),
    CONSTRAINT ck_space_location_not_blank CHECK (BTRIM(location) <> ''),
    CONSTRAINT ck_space_hourly_rate_non_negative CHECK (hourly_rate >= 0)
);

CREATE EXTENSION IF NOT EXISTS btree_gist;

CREATE TABLE IF NOT EXISTS booking.reservation (
    id         BIGSERIAL   PRIMARY KEY,
    account_id BIGINT      NOT NULL,
    space_id   BIGINT      NOT NULL,
    start_at   TIMESTAMPTZ NOT NULL,
    end_at     TIMESTAMPTZ NOT NULL,
    status     VARCHAR(20) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT ck_reservation_period CHECK (end_at > start_at),
    CONSTRAINT ck_reservation_status CHECK (status IN ('PENDING', 'PENDING_PAYMENT', 'CONFIRMED', 'CANCELLED', 'COMPLETED')),
    CONSTRAINT fk_reservation_account
        FOREIGN KEY (account_id) REFERENCES access.account (id),
    CONSTRAINT fk_reservation_space
        FOREIGN KEY (space_id) REFERENCES booking.space (id),
    CONSTRAINT ex_reservation_no_overlap
        EXCLUDE USING gist (
            space_id WITH =,
            tstzrange(start_at, end_at, '[)') WITH &&
        ) WHERE (status <> 'CANCELLED')
);

CREATE INDEX IF NOT EXISTS idx_reservation_account_id ON booking.reservation (account_id);
CREATE INDEX IF NOT EXISTS idx_reservation_space_id ON booking.reservation (space_id);
