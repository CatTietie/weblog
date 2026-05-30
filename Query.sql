CREATE TABLE `t_resume` (
                            `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
                            `user_id` BIGINT NOT NULL COMMENT '用户ID',
                            `name` VARCHAR(50) NOT NULL COMMENT '简历名称',
                            `content` MEDIUMTEXT COMMENT '简历内容(Markdown)',
                            `template_id` VARCHAR(30) NOT NULL DEFAULT 'default' COMMENT '模板ID',
                            `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                            `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                            PRIMARY KEY (`id`),
                            KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='简历表';
