
CREATE TABLE profiles (
                          id BIGSERIAL PRIMARY KEY,
                          email VARCHAR(255) NOT NULL UNIQUE,
                          first_name VARCHAR(100) NOT NULL,
                          last_name VARCHAR(100) NOT NULL,
                          role VARCHAR(20) NOT NULL,
                          validated BOOLEAN NOT NULL DEFAULT FALSE,
                          created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
