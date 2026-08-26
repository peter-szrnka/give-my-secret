CREATE TABLE shedlock (
    name VARCHAR2(64 CHAR) NOT NULL,
    lock_until TIMESTAMP(3) NOT NULL,
    locked_at TIMESTAMP(3) NOT NULL,
    locked_by VARCHAR2(255 CHAR) NOT NULL,
    CONSTRAINT shedlock_pk PRIMARY KEY (name)
);
