-- 敏感词表
CREATE TABLE `t_sensitive_word` (
    `id`          BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    `word`        VARCHAR(100) NOT NULL COMMENT '敏感词',
    `category`    VARCHAR(50) DEFAULT '默认' COMMENT '分类(如: 政治、色情、广告等)',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
    `tenant_id`   BIGINT UNSIGNED DEFAULT 0 NOT NULL,
    UNIQUE KEY `uk_word_tenant` (`word`, `tenant_id`),
    INDEX `idx_category` (`category`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='敏感词库';

-- 敏感词扫描任务表
CREATE TABLE `t_sensitive_scan_task` (
    `id`             BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    `status`         TINYINT(2) DEFAULT 0 NOT NULL COMMENT '0-进行中, 1-已完成, 2-失败',
    `total_articles` INT UNSIGNED DEFAULT 0 COMMENT '待扫描文章总数',
    `total_comments` INT UNSIGNED DEFAULT 0 COMMENT '待扫描评论总数',
    `scanned_count`  INT UNSIGNED DEFAULT 0 COMMENT '已扫描条数',
    `hit_count`      INT UNSIGNED DEFAULT 0 COMMENT '命中条数',
    `create_time`    DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
    `finish_time`    DATETIME DEFAULT NULL COMMENT '完成时间',
    `tenant_id`      BIGINT UNSIGNED DEFAULT 0 NOT NULL,
    INDEX `idx_tenant_status` (`tenant_id`, `status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='敏感词扫描任务';

-- 敏感词扫描结果表
CREATE TABLE `t_sensitive_scan_result` (
    `id`            BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    `task_id`       BIGINT UNSIGNED NOT NULL COMMENT '关联扫描任务ID',
    `target_type`   TINYINT(2) NOT NULL COMMENT '1-文章标题, 2-文章正文, 3-评论',
    `target_id`     BIGINT UNSIGNED NOT NULL COMMENT '文章ID或评论ID',
    `hit_words`     VARCHAR(500) NOT NULL COMMENT '命中的敏感词,逗号分隔',
    `context`       VARCHAR(200) DEFAULT NULL COMMENT '命中位置上下文片段',
    `handled`       TINYINT(1) DEFAULT 0 NOT NULL COMMENT '0-未处理, 1-已忽略, 2-已删除',
    `create_time`   DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
    `tenant_id`     BIGINT UNSIGNED DEFAULT 0 NOT NULL,
    INDEX `idx_task_id` (`task_id`),
    INDEX `idx_target` (`target_type`, `target_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='敏感词扫描结果';

-- 插入默认权限
INSERT INTO `t_permission` (`id`, `parent_id`, `name`, `code`, `type`, `sort`) VALUES
(NULL, 0, '敏感词管理', 'sensitive_word', 1, 100),
(NULL, (SELECT id FROM (SELECT id FROM t_permission WHERE code = 'sensitive_word') AS tmp), '查看敏感词', 'sensitive_word:list', 2, 1),
(NULL, (SELECT id FROM (SELECT id FROM t_permission WHERE code = 'sensitive_word') AS tmp), '添加敏感词', 'sensitive_word:add', 2, 2),
(NULL, (SELECT id FROM (SELECT id FROM t_permission WHERE code = 'sensitive_word') AS tmp), '删除敏感词', 'sensitive_word:delete', 2, 3),
(NULL, (SELECT id FROM (SELECT id FROM t_permission WHERE code = 'sensitive_word') AS tmp), '批量导入', 'sensitive_word:import', 2, 4),
(NULL, (SELECT id FROM (SELECT id FROM t_permission WHERE code = 'sensitive_word') AS tmp), '全库扫描', 'sensitive_word:scan', 2, 5);
