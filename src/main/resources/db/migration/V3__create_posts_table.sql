CREATE TABLE posts (
                       id          BIGSERIAL PRIMARY KEY,
                       title       VARCHAR(200) NOT NULL,
                       content     TEXT NOT NULL,
                       author_id   BIGINT NOT NULL,
                       created_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       updated_at  TIMESTAMP,

                       CONSTRAINT fk_posts_author FOREIGN KEY (author_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX idx_posts_author_id ON posts(author_id);
CREATE INDEX idx_posts_created_at ON posts(created_at);