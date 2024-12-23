SET search_path = 'proc';

--==========================--
-- TABLE process_transition
--==========================--
CREATE TABLE IF NOT EXISTS process_transition (
    step_id     UUID    NOT NULL,
    process_id  UUID    NOT NULL,

    PRIMARY KEY (step_id),

    FOREIGN KEY (step_id)
        REFERENCES step (id)
        ON DELETE CASCADE ON UPDATE RESTRICT,

    FOREIGN KEY (process_id)
        REFERENCES process (id)
        ON DELETE RESTRICT ON UPDATE RESTRICT
);

ALTER TABLE IF EXISTS step_status
    ADD COLUMN deadline DATE;

ALTER TABLE IF EXISTS step_status
    ADD COLUMN completed_at DATE;

ALTER TABLE IF EXISTS step_status
    DROP COLUMN is_completed;
