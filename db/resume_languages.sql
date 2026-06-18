-- 简历多语言支持
ALTER TABLE t_resume ADD COLUMN languages MEDIUMTEXT COMMENT '多语言内容(JSON)' AFTER cover_data;
