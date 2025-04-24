-- Создание таблицы типов документов
CREATE TABLE proc.document_type (
                                    id BIGINT PRIMARY KEY,
                                    name VARCHAR(255) NOT NULL,
                                    description TEXT
);

CREATE TABLE IF NOT EXISTS document_substep (
                                                id              UUID        NOT NULL,
                                                process_id      UUID        NOT NULL,
                                                step_id         INT         NOT NULL,
                                                document_type   VARCHAR(50) NOT NULL,
                                                duration        INT         NOT NULL DEFAULT 1,

                                                PRIMARY KEY (id),

                                                FOREIGN KEY (process_id, step_id)
                                                    REFERENCES step (process_id, id)
                                                    ON DELETE CASCADE ON UPDATE RESTRICT
);

CREATE TABLE IF NOT EXISTS document_substep_status (
                                                       employee_id          UUID    NOT NULL,
                                                       start_process_id     UUID    NOT NULL,
                                                       document_substep_id  UUID    NOT NULL,
                                                       is_completed        BOOLEAN NOT NULL DEFAULT false,

                                                       PRIMARY KEY (employee_id, start_process_id, document_substep_id),

                                                       FOREIGN KEY (employee_id, start_process_id)
                                                           REFERENCES employee_at_process (employee_id, process_id)
                                                           ON DELETE CASCADE ON UPDATE RESTRICT,

                                                       FOREIGN KEY (document_substep_id)
                                                           REFERENCES document_substep (id)
                                                           ON DELETE RESTRICT ON UPDATE RESTRICT
);