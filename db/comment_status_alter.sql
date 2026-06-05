ALTER TABLE t_comment ADD COLUMN `status` TINYINT(2) DEFAULT 1 NOT NULL COMMENT '状态: 0-待审核, 1-已发布' AFTER `is_deleted`;
