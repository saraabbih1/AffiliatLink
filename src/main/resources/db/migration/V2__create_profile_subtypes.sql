
ALTER TABLE profiles
DROP COLUMN first_name,
    DROP COLUMN last_name,
    DROP COLUMN role,
    DROP COLUMN validated,
    ADD COLUMN status VARCHAR(20) NOT NULL DEFAULT 'PENDING';

CREATE TABLE publishers (
                            profile_id BIGINT PRIMARY KEY REFERENCES profiles(id),
                            first_name VARCHAR(100) NOT NULL,
                            last_name VARCHAR(100) NOT NULL,
                            niche VARCHAR(50) NOT NULL,
                            bio VARCHAR(1000),
                            country VARCHAR(100) NOT NULL
);

CREATE TABLE advertisers (
                             profile_id BIGINT PRIMARY KEY REFERENCES profiles(id),
                             company_name VARCHAR(150) NOT NULL,
                             sector VARCHAR(100) NOT NULL,
                             website VARCHAR(255)
);
