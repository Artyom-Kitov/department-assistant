CREATE SCHEMA IF NOT EXISTS templates;

CREATE TABLE templates.files (
    id BIGSERIAL PRIMARY KEY,
    fs_file_name VARCHAR(255),
    file_name VARCHAR(255) NOT NULL,
    file_extension VARCHAR(50) NOT NULL,
    template_type VARCHAR(20) NOT NULL,
    size BIGINT,
    upload_date TIMESTAMP,
    subject_text TEXT
);