CREATE TABLE IF NOT EXISTS users (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    CONSTRAINT uk_users_email UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS services (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    description TEXT NULL,
    module_name VARCHAR(100) NOT NULL,
    status VARCHAR(20) NOT NULL,
    version VARCHAR(50) NOT NULL,
    port INT NOT NULL,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS environments (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    development BIT(1) NOT NULL DEFAULT b'0',
    testing BIT(1) NOT NULL DEFAULT b'0',
    staging BIT(1) NOT NULL DEFAULT b'0',
    production BIT(1) NOT NULL DEFAULT b'0',
    PRIMARY KEY (id),
    CONSTRAINT uk_environments_name UNIQUE (name)
);

CREATE TABLE IF NOT EXISTS deployments (
    id BIGINT NOT NULL AUTO_INCREMENT,
    service_id BIGINT NOT NULL,
    environment_id BIGINT NOT NULL,
    version VARCHAR(50) NOT NULL,
    deployment_status VARCHAR(20) NOT NULL,
    deployment_time DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    INDEX idx_deployments_service_id (service_id),
    INDEX idx_deployments_environment_id (environment_id),
    CONSTRAINT fk_deployments_service FOREIGN KEY (service_id) REFERENCES services (id),
    CONSTRAINT fk_deployments_environment FOREIGN KEY (environment_id) REFERENCES environments (id)
);

CREATE TABLE IF NOT EXISTS metrics (
    id BIGINT NOT NULL AUTO_INCREMENT,
    service_id BIGINT NOT NULL,
    cpu_usage DECIMAL(5,2) NOT NULL,
    memory_usage DECIMAL(5,2) NOT NULL,
    request_count BIGINT NOT NULL,
    error_count BIGINT NOT NULL,
    uptime BIGINT NOT NULL,
    captured_at DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    INDEX idx_metrics_service_captured_at (service_id, captured_at),
    CONSTRAINT fk_metrics_service FOREIGN KEY (service_id) REFERENCES services (id)
);

CREATE TABLE IF NOT EXISTS notifications (
    id BIGINT NOT NULL AUTO_INCREMENT,
    service_id BIGINT NOT NULL,
    message TEXT NOT NULL,
    severity VARCHAR(20) NOT NULL,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    is_read BIT(1) NOT NULL DEFAULT b'0',
    PRIMARY KEY (id),
    INDEX idx_notifications_service_id (service_id),
    CONSTRAINT fk_notifications_service FOREIGN KEY (service_id) REFERENCES services (id)
);

CREATE TABLE IF NOT EXISTS service_environments (
    service_id BIGINT NOT NULL,
    environment_id BIGINT NOT NULL,
    PRIMARY KEY (service_id, environment_id),
    CONSTRAINT fk_service_environments_service FOREIGN KEY (service_id) REFERENCES services (id) ON DELETE CASCADE,
    CONSTRAINT fk_service_environments_environment FOREIGN KEY (environment_id) REFERENCES environments (id) ON DELETE CASCADE
);
