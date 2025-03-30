-- V1_1__insert_levels.sql


INSERT INTO level (level_id, level, min_call_duration, min_report_count, badge_icon, created_at, updated_at)
VALUES (1, 1, 0, 0, '등급 1 이미지', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       (2, 2, 30, 5, '등급 2 이미지', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       (3, 3, 60, 10, '등급 3 이미지', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       (4, 4, 90, 20, '등급 4 이미지', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       (5, 5, 180, 30, '등급 5 이미지', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);