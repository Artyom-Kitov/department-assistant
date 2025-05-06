SET search_path = 'proc';

-- Создаем таблицу для типов документов
CREATE TABLE IF NOT EXISTS document_type (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

-- Добавляем базовые типы документов
INSERT INTO document_type (name)
VALUES ('Документ 1'),
       ('Документ 2');