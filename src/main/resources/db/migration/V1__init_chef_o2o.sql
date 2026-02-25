-- V1__init_chef_o2o.sql

-- 1) accounts (login identity)
CREATE TABLE accounts (
                          id BIGINT PRIMARY KEY AUTO_INCREMENT,
                          username VARCHAR(50) NULL,
                          phone VARCHAR(20) NULL,
                          password_hash VARCHAR(100) NOT NULL,
                          role VARCHAR(20) NOT NULL,         -- ADMIN / USER / CHEF
                          status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',  -- ACTIVE / DISABLED
                          created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                          UNIQUE KEY uk_accounts_username (username),
                          UNIQUE KEY uk_accounts_phone (phone)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 2) chef profile (business profile)
CREATE TABLE chef_profiles (
                               id BIGINT PRIMARY KEY AUTO_INCREMENT,
                               account_id BIGINT NOT NULL,
                               avatar_url VARCHAR(255) NULL,
                               bio VARCHAR(500) NULL,
                               service_area VARCHAR(100) NULL,
                               work_time_desc VARCHAR(100) NULL,     -- e.g. "Mon-Fri 10:00-18:00"
                               years_experience INT NULL,
                               base_price_cents INT NULL,
                               avg_rating DECIMAL(3,2) NOT NULL DEFAULT 0.00,
                               total_orders INT NOT NULL DEFAULT 0,
                               created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                               updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                               CONSTRAINT fk_chef_profiles_account FOREIGN KEY (account_id) REFERENCES accounts(id) ON DELETE CASCADE,
                               UNIQUE KEY uk_chef_profiles_account (account_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 3) cuisine categories
CREATE TABLE cuisine_categories (
                                    id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                    name VARCHAR(50) NOT NULL,
                                    sort_order INT NOT NULL DEFAULT 0,
                                    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                    UNIQUE KEY uk_cuisine_categories_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 4) chef <-> cuisine many-to-many
CREATE TABLE chef_cuisines (
                               chef_id BIGINT NOT NULL,
                               cuisine_id BIGINT NOT NULL,
                               PRIMARY KEY (chef_id, cuisine_id),
                               CONSTRAINT fk_chef_cuisines_chef FOREIGN KEY (chef_id) REFERENCES chef_profiles(id) ON DELETE CASCADE,
                               CONSTRAINT fk_chef_cuisines_cuisine FOREIGN KEY (cuisine_id) REFERENCES cuisine_categories(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 5) service items (what chef sells)
CREATE TABLE service_items (
                               id BIGINT PRIMARY KEY AUTO_INCREMENT,
                               chef_id BIGINT NOT NULL,
                               title VARCHAR(80) NOT NULL,
                               duration_minutes INT NOT NULL,
                               price_cents INT NOT NULL,
                               status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',  -- ACTIVE / OFFLINE
                               created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                               updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                               CONSTRAINT fk_service_items_chef FOREIGN KEY (chef_id) REFERENCES chef_profiles(id) ON DELETE CASCADE,
                               KEY idx_service_items_chef (chef_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 6) bookings/orders
CREATE TABLE orders (
                        id BIGINT PRIMARY KEY AUTO_INCREMENT,
                        user_account_id BIGINT NOT NULL,
                        chef_id BIGINT NOT NULL,
                        service_item_id BIGINT NOT NULL,
                        scheduled_at DATETIME NOT NULL,
                        address VARCHAR(200) NOT NULL,
                        note VARCHAR(300) NULL,
                        status VARCHAR(30) NOT NULL,  -- PENDING_PAYMENT / PAID / CHEF_ACCEPTED / CHEF_REJECTED / IN_PROGRESS / COMPLETED / CANCELLED
                        total_price_cents INT NOT NULL,
                        created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

                        CONSTRAINT fk_orders_user FOREIGN KEY (user_account_id) REFERENCES accounts(id),
                        CONSTRAINT fk_orders_chef FOREIGN KEY (chef_id) REFERENCES chef_profiles(id),
                        CONSTRAINT fk_orders_service FOREIGN KEY (service_item_id) REFERENCES service_items(id),

                        KEY idx_orders_user (user_account_id),
                        KEY idx_orders_chef (chef_id),
                        KEY idx_orders_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 7) reviews
CREATE TABLE reviews (
                         id BIGINT PRIMARY KEY AUTO_INCREMENT,
                         order_id BIGINT NOT NULL,
                         user_account_id BIGINT NOT NULL,
                         chef_id BIGINT NOT NULL,
                         rating INT NOT NULL, -- 1~5
                         comment VARCHAR(500) NULL,
                         created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                         CONSTRAINT fk_reviews_order FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
                         CONSTRAINT fk_reviews_user FOREIGN KEY (user_account_id) REFERENCES accounts(id),
                         CONSTRAINT fk_reviews_chef FOREIGN KEY (chef_id) REFERENCES chef_profiles(id),

                         UNIQUE KEY uk_reviews_order (order_id),
                         KEY idx_reviews_chef (chef_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- seed: cuisine examples + one admin
INSERT INTO cuisine_categories(name, sort_order) VALUES
                                                     ('Sichuan', 1), ('Cantonese', 2), ('Japanese', 3), ('Western', 4);

-- admin account (password should be bcrypt hash from your encoder)
-- Option A: leave empty for now and create via code at startup