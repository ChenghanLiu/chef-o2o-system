CREATE TABLE IF NOT EXISTS news (
                                    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,

                                    title VARCHAR(120) NOT NULL,
    content TEXT NOT NULL,

    status VARCHAR(20) NOT NULL DEFAULT 'PUBLISHED',  -- PUBLISHED / HIDDEN

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    KEY idx_news_status (status),
    KEY idx_news_created_at (created_at)
    );