SET search_path = 'proc';

ALTER TABLE step_status
    ADD COLUMN  start_process_id UUID       NOT NULL,
    ADD COLUMN  is_successful    BOOLEAN,
    ADD CONSTRAINT start_process_fk
        FOREIGN KEY (start_process_id)
            REFERENCES process (id)
            ON DELETE RESTRICT ON UPDATE RESTRICT;