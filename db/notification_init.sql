CREATE TABLE `t_notification` (
    `id`              BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    `receiver_id`     BIGINT UNSIGNED NOT NULL COMMENT '接收人用户ID',
    `type`            TINYINT NOT NULL DEFAULT 1 COMMENT '通知类型: 1=评论回复',
    `title`           VARCHAR(100) NOT NULL COMMENT '通知标题',
    `content`         VARCHAR(300) NOT NULL COMMENT '通知内容摘要',
    `article_id`      BIGINT UNSIGNED DEFAULT NULL COMMENT '关联文章ID',
    `comment_id`      BIGINT UNSIGNED DEFAULT NULL COMMENT '关联评论ID',
    `is_read`         TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否已读: 0=未读, 1=已读',
    `create_time`     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX `idx_receiver_id` (`receiver_id`),
    INDEX `idx_receiver_read` (`receiver_id`, `is_read`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='通知表';
