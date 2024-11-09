CREATE TABLE gms_user (
	id BIGINT NOT NULL IDENTITY(1, 1),
	creation_date datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
	credential VARCHAR(255) NULL DEFAULT NULL,
	email VARCHAR(255) NOT NULL,
	name VARCHAR(255) NOT NULL,
	role VARCHAR(255) NOT NULL,
	status VARCHAR(255) NOT NULL,
	user_name VARCHAR(255) NOT NULL,
	mfa_enabled TINYINT NOT NULL DEFAULT 0,
    mfa_secret VARCHAR(32) NOT NULL,
    failed_attempts INT NOT NULL DEFAULT 0,
	PRIMARY KEY (id)
);

CREATE TABLE gms_api_key (
	id BIGINT NOT NULL IDENTITY(1, 1),
	creation_date datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
	name VARCHAR(255)  NOT NULL,
	description VARCHAR(255) NULL DEFAULT NULL,
	status VARCHAR(255) NOT NULL,
	user_id BIGINT NOT NULL,
	value VARCHAR(512) NOT NULL,
	PRIMARY KEY (id)
);

CREATE TABLE gms_event (
	id BIGINT NOT NULL IDENTITY(1, 1),
	event_date datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
	operation VARCHAR(255) NULL DEFAULT NULL,
	target VARCHAR(255) NULL DEFAULT NULL,
	user_id BIGINT NOT NULL,
	PRIMARY KEY (id)
);

CREATE TABLE gms_keystore (
	id BIGINT NOT NULL IDENTITY(1, 1),
	name VARCHAR(255) NOT NULL,
	creation_date datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
	credential VARCHAR(512) NOT NULL,
	description VARCHAR(255) NULL DEFAULT NULL,
	file_name VARCHAR(255) NULL DEFAULT NULL,
	status VARCHAR(255) NOT NULL,
	type VARCHAR(255) NOT NULL,
	user_id BIGINT NULL DEFAULT NULL,
	PRIMARY KEY (id)
);

CREATE TABLE gms_keystore_alias (
	id BIGINT NOT NULL IDENTITY(1, 1),
	keystore_id BIGINT NULL DEFAULT NULL,
	alias VARCHAR(512) NOT NULL,
	alias_credential VARCHAR(512) NOT NULL,
	description VARCHAR(255) NULL DEFAULT NULL,
	algorithm VARCHAR(64) NULL DEFAULT NULL,
	PRIMARY KEY (id)
);

CREATE TABLE gms_secret (
	id BIGINT NOT NULL IDENTITY(1, 1),
	creation_date datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
	keystore_alias_id BIGINT NOT NULL,
	last_rotated datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
	last_updated datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
	return_decrypted TINYINT NOT NULL DEFAULT 1,
	rotation_enabled TINYINT NOT NULL DEFAULT 1,
	rotation_period VARCHAR(255) NOT NULL,
	secret_id VARCHAR(255) NOT NULL,
	status VARCHAR(255) NULL DEFAULT NULL,
	secret_type VARCHAR(255) NULL DEFAULT NULL,
	user_id BIGINT NOT NULL,
	value VARCHAR(MAX) NOT NULL,
	PRIMARY KEY (id)
);

CREATE TABLE gms_announcement (
	id BIGINT NOT NULL IDENTITY(1, 1),
	author_id BIGINT NOT NULL,
	announcement_date datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
	title VARCHAR(255) NULL DEFAULT NULL,
	description VARCHAR(255) NULL DEFAULT NULL,
	PRIMARY KEY (id)
);

CREATE TABLE gms_message (
	id BIGINT NOT NULL IDENTITY(1, 1),
	user_id BIGINT NOT NULL,
	creation_date datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
	message VARCHAR(255) NULL DEFAULT NULL,
	opened TINYINT NOT NULL DEFAULT 0,
	action_path VARCHAR(255) NULL DEFAULT NULL,
	PRIMARY KEY (id)
);

CREATE TABLE gms_api_key_restriction (
	id BIGINT NOT NULL IDENTITY(1, 1),
	secret_id BIGINT NULL DEFAULT NULL,
	api_key_id BIGINT NOT NULL,
	user_id BIGINT NOT NULL,
	PRIMARY KEY (id)
);

CREATE TABLE gms_system_property (
	id BIGINT NOT NULL IDENTITY(1, 1),
	prop_key VARCHAR(255) NULL DEFAULT NULL,
	prop_value VARCHAR(255) NULL DEFAULT NULL,
	last_modified datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
	PRIMARY KEY (id)
);

CREATE TABLE gms_ip_restriction (
	id BIGINT NOT NULL IDENTITY(1, 1),
	user_id BIGINT NULL DEFAULT NULL,
	secret_id BIGINT NOT NULL,
	status VARCHAR(255) NULL DEFAULT NULL,
	ip_pattern VARCHAR(255) NULL DEFAULT NULL,
	allow TINYINT NOT NULL DEFAULT 0,
	global TINYINT NOT NULL DEFAULT 0,
	creation_date datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
	last_modified datetime NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE gms_job (
    id BIGINT NOT NULL IDENTITY(1, 1),
	creation_date datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
	start_time datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
	end_time datetime NULL,
	duration TINYINT NULL,
	name VARCHAR(255) NOT NULL,
	status VARCHAR(255) NOT NULL,
	message VARCHAR(255) NULL,
	correlation_id VARCHAR(255) NULL,
);

CREATE TABLE gms_system_attribute (
    id BIGINT NOT NULL IDENTITY(1, 1),
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

INSERT INTO gms_system_attribute (name, value) VALUES ('SYSTEM_STATUS', 'NEED_SETUP');