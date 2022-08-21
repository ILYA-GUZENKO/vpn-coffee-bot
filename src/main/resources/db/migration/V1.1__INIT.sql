CREATE TABLE IF NOT EXISTS customer
(
    id                  integer GENERATED ALWAYS AS IDENTITY,
    chat_id             bigint UNIQUE,
    user_name           VARCHAR(32) UNIQUE,
    private_key         VARCHAR(255),
    public_key          VARCHAR(255),
    internal_ip_address VARCHAR(255),
    reg_date            timestamp with time zone NOT NULL,
    next_payment_date   timestamp with time zone,
    config_file         bytea,
    PRIMARY KEY (id)
);
