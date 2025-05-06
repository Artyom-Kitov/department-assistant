SET search_path = 'proc';

-- Добавляем колонку для типа документа в таблицу substep
ALTER TABLE substep
ADD COLUMN document_type_id BIGINT,
ADD CONSTRAINT fk_document_type
    FOREIGN KEY (document_type_id)
    REFERENCES document_type(id)
    ON DELETE SET NULL ON UPDATE RESTRICT;

-- Обновляем таблицу substep_status, добавляя document_type_id
ALTER TABLE substep_status
ADD COLUMN document_type_id BIGINT,
ADD CONSTRAINT fk_document_type_status
    FOREIGN KEY (document_type_id)
    REFERENCES document_type(id)
    ON DELETE SET NULL ON UPDATE RESTRICT;

-- Удаляем старые таблицы, которые больше не нужны
DROP TABLE IF EXISTS document_substep;
DROP TABLE IF EXISTS document_substep_status;

-- Добавляем тестовый процесс с сабтасками
INSERT INTO process (id, name, total_duration)
VALUES ('dddddddd-0000-0000-0000-000000000000', 'Большой тестовый процесс', 12);

-- Шаги процесса
INSERT INTO step (process_id, id, duration, meta_info, type, description)
VALUES 
    ('dddddddd-0000-0000-0000-000000000000', 0, 0, '{}', 0, 'Старт процесса'),
    ('dddddddd-0000-0000-0000-000000000000', 1, 4, '{}', 2, 'Шаг только с обычными сабтасками'),
    ('dddddddd-0000-0000-0000-000000000000', 2, 5, '{}', 2, 'Шаг только с документными сабтасками'),
    ('dddddddd-0000-0000-0000-000000000000', 3, 3, '{}', 2, 'Смешанный шаг'),
    ('dddddddd-0000-0000-0000-000000000000', 4, 0, '{}', 4, 'Финал');

-- Переходы между шагами
INSERT INTO common_transition (process_id, step_id, next_step_id)
VALUES 
    ('dddddddd-0000-0000-0000-000000000000', 0, 1),
    ('dddddddd-0000-0000-0000-000000000000', 1, 2),
    ('dddddddd-0000-0000-0000-000000000000', 2, 3),
    ('dddddddd-0000-0000-0000-000000000000', 3, 4);

-- Финальный статус
INSERT INTO final_type (process_id, step_id, is_successful)
VALUES ('dddddddd-0000-0000-0000-000000000000', 4, true);

-- Сабтаски для шага 1 (только обычные)
INSERT INTO substep (process_id, step_id, id, duration, description, document_type_id)
VALUES 
    ('dddddddd-0000-0000-0000-000000000000', 1, 'dddddddd-0000-0000-0000-000000000001', 2, 'Обычный сабтаск 1', NULL),
    ('dddddddd-0000-0000-0000-000000000000', 1, 'dddddddd-0000-0000-0000-000000000002', 2, 'Обычный сабтаск 2', NULL);

-- Сабтаски для шага 2 (только документные)
INSERT INTO substep (process_id, step_id, id, duration, description, document_type_id)
VALUES 
    ('dddddddd-0000-0000-0000-000000000000', 2, 'dddddddd-0000-0000-0000-000000000003', 3, 'Сабтаск: пиво', 1),
    ('dddddddd-0000-0000-0000-000000000000', 2, 'dddddddd-0000-0000-0000-000000000004', 2, 'Сабтаск: водка', 2);

-- Сабтаски для шага 3 (смешанные)
INSERT INTO substep (process_id, step_id, id, duration, description, document_type_id)
VALUES 
    ('dddddddd-0000-0000-0000-000000000000', 3, 'dddddddd-0000-0000-0000-000000000005', 1, 'Обычный сабтаск 3', NULL),
    ('dddddddd-0000-0000-0000-000000000000', 3, 'dddddddd-0000-0000-0000-000000000006', 2, 'Сабтаск: пиво', 1); 