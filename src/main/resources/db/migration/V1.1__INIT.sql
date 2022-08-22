CREATE TABLE IF NOT EXISTS customer
(
    id                  integer GENERATED ALWAYS AS IDENTITY,
    chat_id             bigint UNIQUE,
    user_name           VARCHAR(32) UNIQUE,
    private_key         VARCHAR(255),
    public_key          VARCHAR(255),
    internal_ip_address VARCHAR(255) UNIQUE,
    reg_date            timestamp with time zone NOT NULL,
    next_payment_date   timestamp with time zone,
    config_file         bytea,
    PRIMARY KEY (id)
);


insert into customer (chat_id, user_name, private_key, public_key, internal_ip_address, reg_date, next_payment_date,
                      config_file)
VALUES (0, 'admin', 'mock', 'mock', '10.0.0.2/32', now(), now(), '6d 6f 63 6b 20 6d 6f 63 6b 20 6d 6f 63 6b 20 6d 6f 63 6b')
