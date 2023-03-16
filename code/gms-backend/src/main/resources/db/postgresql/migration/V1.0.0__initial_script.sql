CREATE TABLE gms_user (
	id SERIAL PRIMARY KEY,
	creation_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	credential VARCHAR(255) NULL DEFAULT NULL,
	email VARCHAR(255) NOT NULL,
	name VARCHAR(255) NOT NULL,
	roles VARCHAR(255) NOT NULL,
	status VARCHAR(255) NOT NULL,
	user_name VARCHAR(255) NOT NULL
);

CREATE TABLE gms_api_key (
	id SERIAL PRIMARY KEY,
	creation_date TIMESTAMP NOT NULL DEFAULT current_timestamp,
	name VARCHAR(255)  NOT NULL,
	description VARCHAR(255) NULL DEFAULT NULL,
	status VARCHAR(255) NOT NULL,
	user_id BIGINT NOT NULL,
	value VARCHAR(512) NOT NULL
);

CREATE TABLE gms_event (
	id SERIAL PRIMARY KEY,
	event_date TIMESTAMP NOT NULL DEFAULT current_timestamp,
	operation VARCHAR(255) NULL DEFAULT NULL,
	target VARCHAR(255) NULL DEFAULT NULL,
	user_id BIGINT NOT NULL
);

CREATE TABLE gms_keystore (
	id SERIAL PRIMARY KEY,
	name VARCHAR(255) NOT NULL,
	creation_date TIMESTAMP NOT NULL DEFAULT current_timestamp,
	credential VARCHAR(512) NOT NULL,
	description VARCHAR(255) NULL DEFAULT NULL,
	file_name VARCHAR(255) NULL DEFAULT NULL,
	status VARCHAR(255) NOT NULL,
	type VARCHAR(255) NOT NULL,
	user_id BIGINT NULL DEFAULT NULL
);

CREATE TABLE gms_keystore_alias (
	id SERIAL PRIMARY KEY,
	keystore_id BIGINT NULL DEFAULT NULL,
	alias VARCHAR(512) NOT NULL,
	alias_credential VARCHAR(512) NOT NULL,
	description VARCHAR(255) NULL DEFAULT NULL
);

CREATE TABLE gms_secret (
	id SERIAL PRIMARY KEY,
	creation_date TIMESTAMP NOT NULL DEFAULT current_timestamp,
	keystore_alias_id BIGINT NOT NULL,
	last_rotated TIMESTAMP NOT NULL DEFAULT current_timestamp,
	last_updated TIMESTAMP NOT NULL DEFAULT current_timestamp,
	return_decrypted INT NOT NULL DEFAULT 1,
	rotation_enabled INT NOT NULL DEFAULT 1,
	rotation_period VARCHAR(255) NOT NULL,
	secret_id VARCHAR(255) NOT NULL,
	status VARCHAR(255) NULL DEFAULT NULL,
	secret_type VARCHAR(255) NOT NULL,
	user_id BIGINT NOT NULL,
	value VARCHAR(512) NOT NULL
);

CREATE TABLE gms_announcement (
	id SERIAL PRIMARY KEY,
	author_id BIGINT NOT NULL,
	announcement_date TIMESTAMP NOT NULL DEFAULT current_timestamp,
	title VARCHAR(255) NULL DEFAULT NULL,
	description VARCHAR(255) NULL DEFAULT NULL
);

CREATE TABLE gms_message (
	id SERIAL PRIMARY KEY,
	user_id BIGINT NOT NULL,
	creation_date TIMESTAMP NOT NULL DEFAULT current_timestamp,
	message VARCHAR(255) NULL DEFAULT NULL,
	opened SMALLINT NOT NULL DEFAULT 0
);

CREATE TABLE gms_api_key_restriction (
	id SERIAL PRIMARY KEY,
	secret_id BIGINT NULL DEFAULT NULL,
	api_key_id BIGINT NOT NULL,
	user_id BIGINT NOT NULL
);

CREATE TABLE gms_system_property (
	id SERIAL PRIMARY KEY,
	key VARCHAR(255) NULL DEFAULT NULL,
	value VARCHAR(255) NULL DEFAULT NULL,
	last_modified TIMESTAMP NOT NULL DEFAULT current_timestamp
);