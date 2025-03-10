CREATE SCHEMA proc;
SET search_path = 'proc';

--==========================--
-- TABLE process
--==========================--
CREATE TABLE IF NOT EXISTS process (
    id              UUID    NOT NULL,
    name            TEXT    NOT NULL,
    total_duration  INT     NOT NULL DEFAULT 0,

    PRIMARY KEY (id)
);

--==========================--
-- TABLE step
--==========================--
CREATE TABLE IF NOT EXISTS step (
    process_id  UUID    NOT NULL,
    id          INT     NOT NULL,
    duration    INT     NOT NULL DEFAULT 1,
    meta_info   JSON,
    type        INT     NOT NULL,
    description TEXT    NOT NULL,

    PRIMARY KEY (id, process_id),

    FOREIGN KEY (process_id)
        REFERENCES process (id)
        ON DELETE CASCADE ON UPDATE RESTRICT
);

--==========================--
-- TABLE employee_at_process
--==========================--
CREATE TABLE IF NOT EXISTS employee_at_process (
    employee_id UUID    NOT NULL,
    process_id  UUID    NOT NULL,
    started_at  DATE    NOT NULL,
    deadline    DATE,

    PRIMARY KEY (employee_id, process_id),

    FOREIGN KEY (employee_id)
        REFERENCES public.employee (id)
        ON DELETE RESTRICT ON UPDATE RESTRICT,

    FOREIGN KEY (process_id)
        REFERENCES process (id)
        ON DELETE RESTRICT ON UPDATE RESTRICT
);

--==========================--
-- TABLE common_transition
--==========================--
CREATE TABLE IF NOT EXISTS common_transition (
    process_id      UUID    NOT NULL,
    step_id         INT     NOT NULL,
    next_step_id    INT     NOT NULL,

    PRIMARY KEY (process_id, step_id),

    FOREIGN KEY (process_id, step_id)
        REFERENCES step (process_id, id)
        ON DELETE CASCADE ON UPDATE RESTRICT,

    FOREIGN KEY (process_id, next_step_id)
            REFERENCES step (process_id, id)
            ON DELETE CASCADE ON UPDATE RESTRICT,

    CHECK (step_id != next_step_id)
);

--==========================--
-- TABLE conditional_transition
--==========================--
CREATE TABLE IF NOT EXISTS conditional_transition (
    process_id          UUID    NOT NULL,
    step_id             INT     NOT NULL,
    positive_step_id    INT     NOT NULL,
    negative_step_id    INT     NOT NULL,

    PRIMARY KEY (process_id, step_id),

    FOREIGN KEY (process_id, step_id)
        REFERENCES step (process_id, id)
        ON DELETE CASCADE ON UPDATE RESTRICT,

    FOREIGN KEY (process_id, positive_step_id)
        REFERENCES step (process_id, id)
        ON DELETE CASCADE ON UPDATE RESTRICT,

    FOREIGN KEY (process_id, negative_step_id)
        REFERENCES step (process_id, id)
        ON DELETE CASCADE ON UPDATE RESTRICT
);

--==========================--
-- TABLE final_type
--==========================--
CREATE TABLE IF NOT EXISTS final_type (
    process_id      UUID    NOT NULL,
    step_id         INT     NOT NULL,
    is_successful   BOOLEAN NOT NULL,

    PRIMARY KEY (process_id, step_id),

    FOREIGN KEY (process_id, step_id)
        REFERENCES step (process_id, id)
        ON DELETE CASCADE ON UPDATE RESTRICT
);

--==========================--
-- TABLE process_transition
--==========================--
CREATE TABLE IF NOT EXISTS process_transition (
    process_id      UUID    NOT NULL,
    step_id         INT     NOT NULL,
    next_process_id UUID    NOT NULL,

    PRIMARY KEY (process_id, step_id),

    FOREIGN KEY (process_id, step_id)
        REFERENCES step (process_id, id)
        ON DELETE CASCADE ON UPDATE RESTRICT,

    FOREIGN KEY (next_process_id)
        REFERENCES process (id)
        ON DELETE RESTRICT ON UPDATE RESTRICT
);

--==========================--
-- TABLE substep
--==========================--
CREATE TABLE IF NOT EXISTS substep (
    process_id  UUID    NOT NULL,
    step_id     INT     NOT NULL,
    id          UUID    NOT NULL,
    duration    INT     NOT NULL DEFAULT 1,
    description TEXT    NOT NULL,

    PRIMARY KEY (id),

    FOREIGN KEY (process_id, step_id)
        REFERENCES step (process_id, id)
        ON DELETE CASCADE ON UPDATE RESTRICT
);

--==========================--
-- TABLE step_status
--==========================--
CREATE TABLE IF NOT EXISTS step_status (
    employee_id         UUID    NOT NULL,
    start_process_id    UUID    NOT NULL,
    process_id          UUID    NOT NULL,
    step_id             INT     NOT NULL,
    deadline            DATE,
    completed_at        DATE,
    is_successful       BOOLEAN,

    PRIMARY KEY (employee_id, start_process_id, process_id, step_id),

    FOREIGN KEY (employee_id, start_process_id)
        REFERENCES employee_at_process (employee_id, process_id)
        ON DELETE CASCADE ON UPDATE RESTRICT,

    FOREIGN KEY (process_id, step_id)
        REFERENCES step (process_id, id)
        ON DELETE RESTRICT ON UPDATE RESTRICT
);

--==========================--
-- TABLE substep_status
--==========================--
CREATE TABLE IF NOT EXISTS substep_status (
    employee_id         UUID    NOT NULL,
    start_process_id    UUID    NOT NULL,
    substep_id          UUID    NOT NULL,
    is_completed        BOOLEAN NOT NULL DEFAULT false,

    PRIMARY KEY (employee_id, substep_id),

    FOREIGN KEY (employee_id, start_process_id)
        REFERENCES employee_at_process (employee_id, process_id)
        ON DELETE CASCADE ON UPDATE RESTRICT,

    FOREIGN KEY (substep_id)
        REFERENCES substep (id)
        ON DELETE RESTRICT ON UPDATE RESTRICT
);