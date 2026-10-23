CREATE TABLE email (
    id         BIGSERIAL PRIMARY KEY,
    recipient  VARCHAR(255) NOT NULL,
    subject    VARCHAR(255) NOT NULL,
    order_code VARCHAR(36)  NOT NULL,
    body       TEXT         NOT NULL
);
