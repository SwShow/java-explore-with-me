DROP TABLE IF EXISTS hits;

CREATE TABLE IF NOT EXISTS hits
(
    id
    BIGINT
    GENERATED
    BY
    DEFAULT AS
    IDENTITY
    PRIMARY
    KEY,
    app
    VARCHAR
(
    50
) NOT NULL,
    ip VARCHAR
(
    20
),
    uri VARCHAR
(
    20
),
    timestamp TIMESTAMP WITHOUT TIME ZONE NOT NULL
    );