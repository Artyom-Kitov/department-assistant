SET search_path = 'proc';

ALTER TABLE execution_history
ADD COLUMN result TEXT NOT NULL DEFAULT '';