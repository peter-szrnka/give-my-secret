CREATE TABLE gms_user (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    creation_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    credential VARCHAR(255) NULL DEFAULT NULL,
    email VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    role VARCHAR(255) NOT NULL,
    status VARCHAR(255) NOT NULL,
    user_name VARCHAR(255) NOT NULL,
    mfa_enabled INTEGER NOT NULL DEFAULT 0,
    mfa_secret VARCHAR(32) NOT NULL,
    failed_attempts INTEGER NOT NULL DEFAULT 0
);

CREATE TABLE gms_api_key (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    creation_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(255) NULL DEFAULT NULL,
    status VARCHAR(255) NOT NULL,
    user_id INTEGER NOT NULL,
    value VARCHAR(512) NOT NULL
);

CREATE TABLE gms_event (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    event_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    operation VARCHAR(255) NULL DEFAULT NULL,
    target VARCHAR(255) NULL DEFAULT NULL,
    user_id INTEGER NOT NULL
);

CREATE TABLE gms_keystore (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name VARCHAR(255) NOT NULL,
    creation_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    credential VARCHAR(512) NOT NULL,
    description VARCHAR(255) NULL DEFAULT NULL,
    file_name VARCHAR(255) NULL DEFAULT NULL,
    status VARCHAR(255) NOT NULL,
    type VARCHAR(255) NOT NULL,
    user_id INTEGER NULL DEFAULT NULL
);

CREATE TABLE gms_keystore_alias (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    keystore_id INTEGER NULL DEFAULT NULL,
    alias VARCHAR(512) NOT NULL,
    alias_credential VARCHAR(512) NOT NULL,
    description VARCHAR(255) NULL DEFAULT NULL,
    algorithm VARCHAR(64) NULL DEFAULT NULL
);

CREATE TABLE gms_secret (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    creation_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    keystore_alias_id INTEGER NOT NULL,
    last_rotated TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_updated TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    return_decrypted INTEGER NOT NULL DEFAULT 1,
    rotation_enabled INTEGER NOT NULL DEFAULT 1,
    rotation_period VARCHAR(255) NOT NULL,
    secret_id VARCHAR(255) NOT NULL,
    status VARCHAR(255) NULL DEFAULT NULL,
    secret_type VARCHAR(255) NOT NULL,
    user_id INTEGER NOT NULL,
    value TEXT NOT NULL
);

CREATE TABLE gms_announcement (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    author_id INTEGER NOT NULL,
    announcement_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    title VARCHAR(255) NULL DEFAULT NULL,
    description VARCHAR(255) NULL DEFAULT NULL
);

CREATE TABLE gms_message (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER NOT NULL,
    creation_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    message VARCHAR(255) NULL DEFAULT NULL,
    opened INTEGER NOT NULL DEFAULT 0,
    action_path VARCHAR(255) NULL DEFAULT NULL
);

CREATE TABLE gms_api_key_restriction (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    secret_id INTEGER NULL DEFAULT NULL,
    api_key_id INTEGER NOT NULL,
    user_id INTEGER NOT NULL
);

CREATE TABLE gms_system_property (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    prop_key VARCHAR(255) NULL DEFAULT NULL,
    prop_value VARCHAR(255) NULL DEFAULT NULL,
    last_modified TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE gms_ip_restriction (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER NULL DEFAULT NULL,
    secret_id INTEGER NULL DEFAULT NULL,
    status VARCHAR(255) NULL DEFAULT NULL,
    ip_pattern VARCHAR(255) NULL DEFAULT NULL,
    allow INTEGER NOT NULL DEFAULT 0,
    global INTEGER NOT NULL DEFAULT 0,
    creation_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_modified TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE gms_job (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    creation_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP NULL,
    duration INTEGER NULL,
    name VARCHAR(255) NOT NULL,
    status VARCHAR(255) NOT NULL,
    message VARCHAR(255) NULL,
    correlation_id VARCHAR(255) NULL
);

CREATE TABLE gms_system_attribute (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name VARCHAR(255) NOT NULL,
    value VARCHAR(255) NOT NULL
);


CREATE INDEX idx_gms_user ON gms_user(id);
CREATE INDEX idx_gms_api_key ON gms_api_key(id);
CREATE INDEX idx_gms_event ON gms_event(id);
CREATE INDEX idx_gms_keystore ON gms_keystore(id);
CREATE INDEX idx_gms_ks_alias ON gms_keystore_alias(id);
CREATE INDEX idx_gms_secret ON gms_secret(id);
CREATE INDEX idx_gms_announcement ON gms_announcement(id);
CREATE INDEX idx_gms_message ON gms_message(id);
CREATE INDEX idx_gms_api_kr ON gms_api_key_restriction(id);
CREATE INDEX idx_gms_sys_prop ON gms_system_property(id);
CREATE INDEX idx_gms_ip_restr ON gms_ip_restriction(id);
CREATE INDEX idx_gms_job ON gms_job(id);
CREATE INDEX idx_gms_sys_attr ON gms_system_attribute(id);

CREATE UNIQUE INDEX idx_unq_gms_user ON gms_user(id);
CREATE UNIQUE INDEX idx_unq_gms_api_key ON gms_api_key(id);
CREATE UNIQUE INDEX idx_unq_gms_event ON gms_event(id);
CREATE UNIQUE INDEX idx_unq_gms_keystore ON gms_keystore(id);
CREATE UNIQUE INDEX idx_unq_gms_ks_alias ON gms_keystore_alias(id);
CREATE UNIQUE INDEX idx_unq_gms_secret ON gms_secret(id);
CREATE UNIQUE INDEX id_unq_gms_announcement ON gms_announcement(id);
CREATE UNIQUE INDEX idx_unq_gms_message ON gms_message(id);
CREATE UNIQUE INDEX idx_unq_gms_api_kr ON gms_api_key_restriction(id);
CREATE UNIQUE INDEX idx_unq_gms_sys_prop ON gms_system_property(id);
CREATE UNIQUE INDEX idx_unq_gms_ip_restr ON gms_ip_restriction(id);
CREATE UNIQUE INDEX idx_unq_gms_job ON gms_job(id);
CREATE UNIQUE INDEX idx_unq_gms_sys_attr ON gms_system_attribute(name);

-- Initial Data
INSERT INTO gms_system_attribute (name, value) VALUES ('SYSTEM_STATUS', 'NEED_SETUP');
