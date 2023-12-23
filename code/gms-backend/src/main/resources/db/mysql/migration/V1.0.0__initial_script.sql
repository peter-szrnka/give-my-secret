CREATE TABLE gms_user (
	id BIGINT NOT NULL AUTO_INCREMENT,
	creation_date TIMESTAMP NOT NULL DEFAULT current_timestamp(),
	credential VARCHAR(255) NULL DEFAULT NULL COLLATE 'utf8mb4_general_ci',
	email VARCHAR(255) NOT NULL COLLATE 'utf8mb4_general_ci',
	name VARCHAR(255) NOT NULL COLLATE 'utf8mb4_general_ci',
	roles VARCHAR(255) NOT NULL COLLATE 'utf8mb4_general_ci',
	status VARCHAR(255) NOT NULL COLLATE 'utf8mb4_general_ci',
	user_name VARCHAR(255) NOT NULL COLLATE 'utf8mb4_general_ci',
	PRIMARY KEY (id) USING BTREE
)
COLLATE='utf8mb4_general_ci' ENGINE=InnoDB;

CREATE TABLE gms_api_key (
	id BIGINT NOT NULL AUTO_INCREMENT,
	creation_date TIMESTAMP NOT NULL DEFAULT current_timestamp(),
	name VARCHAR(255)  NOT NULL COLLATE 'utf8mb4_general_ci',
	description VARCHAR(255) NULL DEFAULT NULL COLLATE 'utf8mb4_general_ci',
	status VARCHAR(255) NOT NULL COLLATE 'utf8mb4_general_ci',
	user_id BIGINT NOT NULL COLLATE 'utf8mb4_general_ci',
	value VARCHAR(512) NOT NULL COLLATE 'utf8mb4_general_ci',
	PRIMARY KEY (id) USING BTREE
)
COLLATE='utf8mb4_general_ci' ENGINE=InnoDB;

CREATE TABLE gms_event (
	id BIGINT NOT NULL AUTO_INCREMENT,
	event_date TIMESTAMP NOT NULL DEFAULT current_timestamp(),
	operation VARCHAR(255) NULL DEFAULT NULL COLLATE 'utf8mb4_general_ci',
	target VARCHAR(255) NULL DEFAULT NULL COLLATE 'utf8mb4_general_ci',
	user_id BIGINT NOT NULL DEFAULT NULL COLLATE 'utf8mb4_general_ci',
	PRIMARY KEY (id) USING BTREE
)
COLLATE='utf8mb4_general_ci' ENGINE=InnoDB;

CREATE TABLE gms_keystore (
	id BIGINT(20) NOT NULL AUTO_INCREMENT,
	name VARCHAR(255) NOT NULL COLLATE 'utf8mb3_general_ci',
	creation_date TIMESTAMP NOT NULL DEFAULT current_timestamp(),
	credential VARCHAR(512) NOT NULL COLLATE 'utf8mb3_general_ci',
	description VARCHAR(255) NULL DEFAULT NULL COLLATE 'utf8mb3_general_ci',
	file_name VARCHAR(255) NULL DEFAULT NULL COLLATE 'utf8mb3_general_ci',
	status VARCHAR(255) NOT NULL COLLATE 'utf8mb3_general_ci',
	type VARCHAR(255) NOT NULL COLLATE 'utf8mb3_general_ci',
	user_id BIGINT(20) NULL DEFAULT NULL,
	PRIMARY KEY (id) USING BTREE
)
COLLATE='utf8mb3_general_ci' ENGINE=InnoDB;

CREATE TABLE gms_keystore_alias (
	id BIGINT(20) NOT NULL AUTO_INCREMENT,
	keystore_id BIGINT(20) NULL DEFAULT NULL,
	alias VARCHAR(512) NOT NULL COLLATE 'utf8mb3_general_ci',
	alias_credential VARCHAR(512) NOT NULL COLLATE 'utf8mb3_general_ci',
	description VARCHAR(255) NULL DEFAULT NULL COLLATE 'utf8mb3_general_ci',
	algorithm VARCHAR(64) NULL DEFAULT NULL COLLATE 'utf8mb3_general_ci',
	
	PRIMARY KEY (id) USING BTREE
)
COLLATE='utf8mb3_general_ci' ENGINE=InnoDB;

CREATE TABLE gms_secret (
	id BIGINT NOT NULL AUTO_INCREMENT,
	creation_date TIMESTAMP NOT NULL DEFAULT current_timestamp(),
	keystore_alias_id BIGINT NOT NULL,
	last_rotated TIMESTAMP NOT NULL DEFAULT current_timestamp(),
	last_updated TIMESTAMP NOT NULL DEFAULT current_timestamp(),
	return_decrypted INT NOT NULL DEFAULT 1,
	rotation_enabled INT NOT NULL DEFAULT 1,
	rotation_period VARCHAR(255) NOT NULL COLLATE 'utf8mb4_general_ci',
	secret_id VARCHAR(255) NOT NULL COLLATE 'utf8mb4_general_ci',
	status VARCHAR(255) NULL DEFAULT NULL COLLATE 'utf8mb4_general_ci',
	secret_type VARCHAR(255) NULL DEFAULT NULL COLLATE 'utf8mb4_general_ci',
	user_id BIGINT NOT NULL,
	value VARCHAR(512) NOT NULL COLLATE 'utf8mb4_general_ci',
	PRIMARY KEY (id) USING BTREE
)
COLLATE='utf8mb4_general_ci' ENGINE=InnoDB;

CREATE TABLE gms_announcement (
	id BIGINT NOT NULL AUTO_INCREMENT,
	author_id BIGINT NOT NULL,
	announcement_date TIMESTAMP NOT NULL DEFAULT current_timestamp(),
	title VARCHAR(255) NULL DEFAULT NULL COLLATE 'utf8mb4_general_ci',
	description VARCHAR(255) NULL DEFAULT NULL COLLATE 'utf8mb4_general_ci',
	PRIMARY KEY (id) USING BTREE
)
COLLATE='utf8mb4_general_ci' ENGINE=InnoDB;

CREATE TABLE gms_message (
	id BIGINT NOT NULL AUTO_INCREMENT,
	user_id BIGINT NOT NULL,
	creation_date TIMESTAMP NOT NULL DEFAULT current_timestamp(),
	message VARCHAR(255) NULL DEFAULT NULL,
	opened TINYINT NOT NULL DEFAULT 0,
	PRIMARY KEY (id) USING BTREE
)
COLLATE='utf8mb4_general_ci' ENGINE=InnoDB;

CREATE TABLE gms_api_key_restriction (
	id BIGINT NOT NULL AUTO_INCREMENT,
	secret_id BIGINT NULL DEFAULT NULL,
	api_key_id BIGINT NOT NULL,
	user_id BIGINT NOT NULL,
	PRIMARY KEY (id) USING BTREE
)
COLLATE='utf8mb4_general_ci' ENGINE=InnoDB;

CREATE TABLE gms_system_property (
	id BIGINT NOT NULL AUTO_INCREMENT,
	key VARCHAR(255) NULL DEFAULT NULL,
	value VARCHAR(255) NULL DEFAULT NULL COLLATE 'utf8mb3_general_ci',
	last_modified TIMESTAMP NOT NULL DEFAULT current_timestamp(),
	PRIMARY KEY (id) USING BTREE
)
COLLATE='utf8mb4_general_ci' ENGINE=InnoDB;

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
