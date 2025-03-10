SET search_path = 'proc';

--==========================--
-- TABLE execution_history
--==========================--
CREATE TABLE IF NOT EXISTS execution_history (
    id              UUID    NOT NULL,
    employee_id     UUID    NOT NULL,
    process_id      UUID    NOT NULL,
    started_at      DATE    NOT NULL,
    completed_at    DATE    NOT NULL,
    is_successful   BOOLEAN NOT NULL,

    PRIMARY KEY (id),

    FOREIGN KEY (employee_id)
        REFERENCES public.employee (id)
        ON DELETE CASCADE ON UPDATE RESTRICT,

    FOREIGN KEY (process_id)
        REFERENCES process (id)
        ON DELETE CASCADE ON UPDATE RESTRICT,

    CONSTRAINT valid_dates
        CHECK (started_at <= completed_at)
);
