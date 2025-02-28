BEGIN TRANSACTION;

SET search_path = 'proc';

INSERT INTO process (id, name, total_duration)
VALUES  ('bbbbbbbb-0000-0000-0000-000000000000', 'Test subprocess', 7),
        ('aaaaaaaa-0000-0000-0000-000000000000', 'Test process', 13);

INSERT INTO step (process_id, id, duration, meta_info, type, description)
VALUES  ('bbbbbbbb-0000-0000-0000-000000000000', 1, 1, '{}', 3, 'P2-1 conditional'),
        ('bbbbbbbb-0000-0000-0000-000000000000', 2, 2, '{}', 1, 'P2-2 common'),
        ('bbbbbbbb-0000-0000-0000-000000000000', 3, 3, '{}', 1, 'P2-3 common'),
        ('bbbbbbbb-0000-0000-0000-000000000000', 4, 1, '{}', 1, 'P2-4 common'),
        ('bbbbbbbb-0000-0000-0000-000000000000', 5, 1, '{}', 3, 'P2-5 conditional'),
        ('bbbbbbbb-0000-0000-0000-000000000000', 6, 0, '{}', 4, 'P2-5 final'),
        ('bbbbbbbb-0000-0000-0000-000000000000', 7, 0, '{}', 4, 'P2-6 final'),
        ('aaaaaaaa-0000-0000-0000-000000000000', 1, 1, '{}', 1, 'P1-1 common'),
        ('aaaaaaaa-0000-0000-0000-000000000000', 2, 1, '{}', 3, 'P1-2 conditional'),
        ('aaaaaaaa-0000-0000-0000-000000000000', 3, 0, '{}', 4, 'P1-3 final'),
        ('aaaaaaaa-0000-0000-0000-000000000000', 4, 3, '{}', 2, 'P1-4 subtasks'),
        ('aaaaaaaa-0000-0000-0000-000000000000', 5, 1, '{}', 3, 'P1-5 conditional'),
        ('aaaaaaaa-0000-0000-0000-000000000000', 6, 0, '{}', 4, 'P1-6 final'),
        ('aaaaaaaa-0000-0000-0000-000000000000', 7, 0, '{}', 5, 'P1-7 transition');

INSERT INTO common_transition (process_id, step_id, next_step_id)
VALUES  ('bbbbbbbb-0000-0000-0000-000000000000', 2, 4),
        ('bbbbbbbb-0000-0000-0000-000000000000', 3, 4),
        ('bbbbbbbb-0000-0000-0000-000000000000', 4, 5),
        ('aaaaaaaa-0000-0000-0000-000000000000', 1, 2),
        ('aaaaaaaa-0000-0000-0000-000000000000', 4, 5);

INSERT INTO conditional_transition (process_id, step_id, positive_step_id, negative_step_id)
VALUES  ('bbbbbbbb-0000-0000-0000-000000000000', 1, 2, 3),
        ('bbbbbbbb-0000-0000-0000-000000000000', 5, 6, 7),
        ('aaaaaaaa-0000-0000-0000-000000000000', 2, 4, 3),
        ('aaaaaaaa-0000-0000-0000-000000000000', 5, 6, 7);

INSERT INTO final_type (process_id, step_id, is_successful)
VALUES  ('bbbbbbbb-0000-0000-0000-000000000000', 6, true),
        ('bbbbbbbb-0000-0000-0000-000000000000', 7, false),
        ('aaaaaaaa-0000-0000-0000-000000000000', 3, false),
        ('aaaaaaaa-0000-0000-0000-000000000000', 6, true);

INSERT INTO process_transition (process_id, step_id, next_process_id)
VALUES  ('aaaaaaaa-0000-0000-0000-000000000000', 7, 'bbbbbbbb-0000-0000-0000-000000000000');

INSERT INTO substep (process_id, step_id, id, duration, description)
VALUES  ('aaaaaaaa-0000-0000-0000-000000000000', 4, 'aaaaaaaa-0000-0000-0000-000000000001', 2, 'P1-4-1 subtask'),
        ('aaaaaaaa-0000-0000-0000-000000000000', 4, 'aaaaaaaa-0000-0000-0000-000000000002', 3, 'P1-4-2 subtask');

COMMIT;