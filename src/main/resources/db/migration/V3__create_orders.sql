CREATE TABLE IF NOT EXISTS orders (
                                      id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,

                                      customer_id BIGINT NOT NULL,
                                      chef_profile_id BIGINT NOT NULL,

                                      service_at TIMESTAMP NOT NULL,

                                      service_address VARCHAR(255) NOT NULL,
    contact_name VARCHAR(50) NOT NULL,
    contact_phone VARCHAR(20) NOT NULL,

    price_cents INT NOT NULL,
    note VARCHAR(500) NULL,

    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    KEY idx_orders_customer_id (customer_id),
    KEY idx_orders_chef_profile_id (chef_profile_id),
    KEY idx_orders_status (status),
    KEY idx_orders_created_at (created_at),

    CONSTRAINT fk_orders_customer FOREIGN KEY (customer_id) REFERENCES accounts(id),
    CONSTRAINT fk_orders_chef_profile FOREIGN KEY (chef_profile_id) REFERENCES chef_profiles(id)
    );