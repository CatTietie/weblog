-- 简历分享功能：添加分享码和分享开关字段
ALTER TABLE t_resume ADD COLUMN share_code VARCHAR(12) DEFAULT NULL COMMENT '分享码(唯一)' AFTER languages;
ALTER TABLE t_resume ADD COLUMN share_enabled TINYINT NOT NULL DEFAULT 0 COMMENT '是否开启分享(0=关闭,1=开启)' AFTER share_code;
ALTER TABLE t_resume ADD UNIQUE INDEX uk_share_code (share_code);
