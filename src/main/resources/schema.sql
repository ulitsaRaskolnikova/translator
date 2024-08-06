CREATE TABLE IF NOT EXISTS translation (
    id BIGSERIAL,
    ip VARCHAR(15) NOT NULL,
    source_language VARCHAR(10) NOT NULL,
    target_language VARCHAR(10) NOT NULL,
    input_text TEXT NOT NULL,
    output_text TEXT NOT NULL,
    created TIMESTAMP DEFAULT now()
);
