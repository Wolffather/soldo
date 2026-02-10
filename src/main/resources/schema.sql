CREATE SCHEMA IF NOT EXISTS soldo_db

CREATE TABLE IF NOT EXISTS soldo_db.events (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    type VARCHAR(255) NOT NULL,
    start_date TIMESTAMP NOT NULL,
    end_date TIMESTAMP NOT NULL,
    price DOUBLE PRECISION,
    num_of_participants INTEGER,
    description VARCHAR(1000)
);