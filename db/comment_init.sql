CREATE TABLE `t_comment` (
    `id`               BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    `article_id`       BIGINT UNSIGNED NOT NULL COMMENT '文章ID',
    `user_id`          BIGINT UNSIGNED NOT NULL COMMENT '评论者用户ID',
    `content`          VARCHAR(500)    NOT NULL COMMENT '评论内容',
    `parent_id`        BIGINT UNSIGNED DEFAULT NULL COMMENT '父评论ID（NULL=根评论）',
    `reply_to_user_id` BIGINT UNSIGNED DEFAULT NULL COMMENT '被回复用户ID',
    `like_count`       INT UNSIGNED    DEFAULT 0 NOT NULL COMMENT '点赞数',
    `is_deleted`       TINYINT(2)      DEFAULT 0 NOT NULL COMMENT '删除标志',
    `create_time`      DATETIME        DEFAULT CURRENT_TIMESTAMP NOT NULL,
    INDEX `idx_article_id` (`article_id`),
    INDEX `idx_parent_id` (`parent_id`)
) COMMENT='评论表' CHARSET=utf8mb4;
