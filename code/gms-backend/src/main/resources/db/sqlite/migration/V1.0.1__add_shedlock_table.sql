CREATE TABLE shedlock (
    name TEXT NOT NULL,
    lock_until TEXT NOT NULL,
    locked_at TEXT NOT NULL,
    locked_by TEXT NOT NULL,
    PRIMARY KEY (name)
);
