CREATE TABLE "user" (
    id            BIGSERIAL PRIMARY KEY,
    name          VARCHAR(500) NOT NULL,
    date_of_birth DATE         NOT NULL,
    password      VARCHAR(500) NOT NULL
);

CREATE TABLE account (
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT  NOT NULL UNIQUE REFERENCES "user" (id),
    balance         DECIMAL NOT NULL,
    start_deposit DECIMAL NOT NULL,
    CONSTRAINT non_negative_balance CHECK (balance >= 0)
);

CREATE TABLE email_data (
    id      BIGSERIAL PRIMARY KEY,
    user_id BIGINT       NOT NULL REFERENCES "user" (id),
    email   VARCHAR(200) NOT NULL UNIQUE,
    CONSTRAINT valid_email CHECK (email ~* '^[A-Za-z0-9._%-]+@[A-Za-z0-9.-]+[.][A-Za-z]+$')
);

CREATE TABLE phone_data (
    id      BIGSERIAL PRIMARY KEY,
    user_id BIGINT      NOT NULL REFERENCES "user" (id),
    phone   VARCHAR(13) NOT NULL UNIQUE,
    CONSTRAINT valid_phone CHECK (phone ~ '^\+?[0-9]{11}$')
);

CREATE INDEX idx_email_data_user_id ON email_data (user_id);
CREATE INDEX idx_phone_data_user_id ON phone_data(user_id);