-- V1 schema for GymOnGo

CREATE TABLE app_user (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(200),
    email VARCHAR(200),
    role VARCHAR(50) DEFAULT 'USER',
    created_at TIMESTAMP DEFAULT now()
);

CREATE TABLE gym (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    address TEXT,
    capacity INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP DEFAULT now()
);

CREATE TABLE subscription (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES app_user(id) ON DELETE CASCADE,
    gym_id BIGINT NOT NULL REFERENCES gym(id) ON DELETE CASCADE,
    started_at TIMESTAMP NOT NULL,
    ends_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT now()
);

CREATE TABLE booking (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES app_user(id) ON DELETE CASCADE,
    gym_id BIGINT NOT NULL REFERENCES gym(id) ON DELETE CASCADE,
    start_time TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT now()
);

-- Indexes to help counting bookings per gym+time
CREATE INDEX idx_booking_gym_start_time ON booking(gym_id, start_time);
