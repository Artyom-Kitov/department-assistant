SET search_path = 'proc';

ALTER TABLE employee_at_process
ADD COLUMN step_process_id  UUID    NOT NULL,
ADD COLUMN step_id          INT     NOT NULL,
ADD CONSTRAINT step_fk
FOREIGN KEY (step_process_id, step_id)
    REFERENCES step (process_id, id)
    ON DELETE RESTRICT ON UPDATE RESTRICT;