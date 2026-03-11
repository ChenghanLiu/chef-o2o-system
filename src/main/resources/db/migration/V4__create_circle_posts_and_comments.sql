CREATE TABLE IF NOT EXISTS circle_posts (
                                            id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                                            author_account_id BIGINT NOT NULL,
                                            title VARCHAR(120) NULL,
    content TEXT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PUBLISHED',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    KEY idx_circle_posts_author (author_account_id),
    KEY idx_circle_posts_status (status),
    KEY idx_circle_posts_created_at (created_at),

    CONSTRAINT fk_circle_posts_author
    FOREIGN KEY (author_account_id) REFERENCES accounts(id)
    );

CREATE TABLE IF NOT EXISTS circle_comments (
                                               id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                                               post_id BIGINT NOT NULL,
                                               author_account_id BIGINT NOT NULL,
                                               content VARCHAR(800) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PUBLISHED',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    KEY idx_circle_comments_post (post_id),
    KEY idx_circle_comments_author (author_account_id),
    KEY idx_circle_comments_status (status),
    KEY idx_circle_comments_created_at (created_at),

    CONSTRAINT fk_circle_comments_post
    FOREIGN KEY (post_id) REFERENCES circle_posts(id) ON DELETE CASCADE,
    CONSTRAINT fk_circle_comments_author
    FOREIGN KEY (author_account_id) REFERENCES accounts(id)
    );