-- 投递过期提醒日志表
CREATE TABLE IF NOT EXISTS `t_application_reminder_log` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `execute_time` DATETIME NOT NULL COMMENT '任务执行时间',
    `scanned_count` INT NOT NULL DEFAULT 0 COMMENT '扫描到的过期投递数',
    `user_count` INT NOT NULL DEFAULT 0 COMMENT '涉及用户数',
    `sent_count` INT NOT NULL DEFAULT 0 COMMENT '成功发送邮件数',
    `failed_count` INT NOT NULL DEFAULT 0 COMMENT '发送失败数',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    INDEX `idx_execute_time` (`execute_time` DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='投递过期提醒执行日志';
