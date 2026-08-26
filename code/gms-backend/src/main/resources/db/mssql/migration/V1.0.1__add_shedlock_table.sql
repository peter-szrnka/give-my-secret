CREATE TABLE shedlock (
    name NVARCHAR(64) NOT NULL,
    lock_until DATETIME2(3) NOT NULL,
    locked_at DATETIME2(3) NOT NULL,
    locked_by NVARCHAR(255) NOT NULL,
    CONSTRAINT PK_shedlock PRIMARY KEY (name)
);
