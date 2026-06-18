USE weblog;

-- 为文章表添加状态字段：0-草稿 1-已发布
ALTER TABLE `t_article` ADD COLUMN `status` tinyint(2) NOT NULL DEFAULT 0 COMMENT '文章状态：0-草稿 1-已发布' AFTER `read_num`;

-- 将现有文章全部设置为已发布（向后兼容）
UPDATE `t_article` SET `status` = 1 WHERE `is_deleted` = 0;

-- 文章版本快照表
CREATE TABLE `t_article_version` (
    `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '版本ID',
    `article_id` bigint unsigned NOT NULL COMMENT '文章ID',
    `title` varchar(120) NOT NULL DEFAULT '' COMMENT '版本标题快照',
    `content` mediumtext NOT NULL COMMENT '版本内容快照',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '版本创建时间',
    PRIMARY KEY (`id`),
    INDEX `idx_article_id` (`article_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文章版本快照表';
