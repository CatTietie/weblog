-- 多租户 SaaS 化改造数据库迁移脚本
-- 日期: 2026-06-04

SET NAMES utf8mb4;

-- =====================================================
-- 1. 创建租户表
-- =====================================================
CREATE TABLE IF NOT EXISTS `t_tenant` (
    `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '租户ID',
    `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '租户名称',
    `domain` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '绑定域名',
    `logo` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '租户Logo',
    `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '租户描述',
    `status` tinyint(2) NOT NULL DEFAULT 0 COMMENT '状态：0-启用，1-禁用',
    `admin_user_id` bigint(20) UNSIGNED NULL COMMENT '管理员用户ID',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `uk_domain`(`domain`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '租户表' ROW_FORMAT = DYNAMIC;

-- =====================================================
-- 2. 插入默认租户（兼容历史数据）
-- =====================================================
INSERT INTO `t_tenant` (`id`, `name`, `domain`, `logo`, `description`, `status`, `create_time`)
VALUES (1, '默认站点', 'localhost', '', '系统默认租户', 0, NOW());

-- =====================================================
-- 3. 为所有核心业务表添加 tenant_id 字段
-- =====================================================

-- t_article
ALTER TABLE `t_article` ADD COLUMN `tenant_id` bigint(20) UNSIGNED NOT NULL DEFAULT 1 COMMENT '租户ID';
ALTER TABLE `t_article` ADD INDEX `idx_tenant_id`(`tenant_id`) USING BTREE;

-- t_article_content
ALTER TABLE `t_article_content` ADD COLUMN `tenant_id` bigint(20) UNSIGNED NOT NULL DEFAULT 1 COMMENT '租户ID';
ALTER TABLE `t_article_content` ADD INDEX `idx_tenant_id`(`tenant_id`) USING BTREE;

-- t_article_category_rel
ALTER TABLE `t_article_category_rel` ADD COLUMN `tenant_id` bigint(20) UNSIGNED NOT NULL DEFAULT 1 COMMENT '租户ID';
ALTER TABLE `t_article_category_rel` ADD INDEX `idx_tenant_id`(`tenant_id`) USING BTREE;

-- t_article_tag_rel
ALTER TABLE `t_article_tag_rel` ADD COLUMN `tenant_id` bigint(20) UNSIGNED NOT NULL DEFAULT 1 COMMENT '租户ID';
ALTER TABLE `t_article_tag_rel` ADD INDEX `idx_tenant_id`(`tenant_id`) USING BTREE;

-- t_article_update_history
ALTER TABLE `t_article_update_history` ADD COLUMN `tenant_id` bigint(20) UNSIGNED NOT NULL DEFAULT 1 COMMENT '租户ID';
ALTER TABLE `t_article_update_history` ADD INDEX `idx_tenant_id`(`tenant_id`) USING BTREE;

-- t_article_version
ALTER TABLE `t_article_version` ADD COLUMN `tenant_id` bigint(20) UNSIGNED NOT NULL DEFAULT 1 COMMENT '租户ID';
ALTER TABLE `t_article_version` ADD INDEX `idx_tenant_id`(`tenant_id`) USING BTREE;

-- t_category
ALTER TABLE `t_category` ADD COLUMN `tenant_id` bigint(20) UNSIGNED NOT NULL DEFAULT 1 COMMENT '租户ID';
ALTER TABLE `t_category` ADD INDEX `idx_tenant_id`(`tenant_id`) USING BTREE;
-- 分类名在租户内唯一，移除全局唯一约束，改为租户内唯一
ALTER TABLE `t_category` DROP INDEX `uk_name`;
ALTER TABLE `t_category` ADD UNIQUE INDEX `uk_tenant_name`(`tenant_id`, `name`) USING BTREE;

-- t_tag
ALTER TABLE `t_tag` ADD COLUMN `tenant_id` bigint(20) UNSIGNED NOT NULL DEFAULT 1 COMMENT '租户ID';
ALTER TABLE `t_tag` ADD INDEX `idx_tenant_id`(`tenant_id`) USING BTREE;
-- 标签名在租户内唯一
ALTER TABLE `t_tag` DROP INDEX `uk_name`;
ALTER TABLE `t_tag` ADD UNIQUE INDEX `uk_tenant_name`(`tenant_id`, `name`) USING BTREE;

-- t_user
ALTER TABLE `t_user` ADD COLUMN `tenant_id` bigint(20) UNSIGNED NOT NULL DEFAULT 1 COMMENT '租户ID';
ALTER TABLE `t_user` ADD INDEX `idx_tenant_id`(`tenant_id`) USING BTREE;

-- t_blog_settings
ALTER TABLE `t_blog_settings` ADD COLUMN `tenant_id` bigint(20) UNSIGNED NOT NULL DEFAULT 1 COMMENT '租户ID';
ALTER TABLE `t_blog_settings` ADD INDEX `idx_tenant_id`(`tenant_id`) USING BTREE;

-- t_comment
ALTER TABLE `t_comment` ADD COLUMN `tenant_id` bigint(20) UNSIGNED NOT NULL DEFAULT 1 COMMENT '租户ID';
ALTER TABLE `t_comment` ADD INDEX `idx_tenant_id`(`tenant_id`) USING BTREE;

-- t_notification
ALTER TABLE `t_notification` ADD COLUMN `tenant_id` bigint(20) UNSIGNED NOT NULL DEFAULT 1 COMMENT '租户ID';
ALTER TABLE `t_notification` ADD INDEX `idx_tenant_id`(`tenant_id`) USING BTREE;

-- t_statistics_article_pv
ALTER TABLE `t_statistics_article_pv` ADD COLUMN `tenant_id` bigint(20) UNSIGNED NOT NULL DEFAULT 1 COMMENT '租户ID';
ALTER TABLE `t_statistics_article_pv` ADD INDEX `idx_tenant_id`(`tenant_id`) USING BTREE;

-- t_resume
ALTER TABLE `t_resume` ADD COLUMN `tenant_id` bigint(20) UNSIGNED NOT NULL DEFAULT 1 COMMENT '租户ID';
ALTER TABLE `t_resume` ADD INDEX `idx_tenant_id`(`tenant_id`) USING BTREE;

-- t_resume_application
ALTER TABLE `t_resume_application` ADD COLUMN `tenant_id` bigint(20) UNSIGNED NOT NULL DEFAULT 1 COMMENT '租户ID';
ALTER TABLE `t_resume_application` ADD INDEX `idx_tenant_id`(`tenant_id`) USING BTREE;

-- t_user_profile
ALTER TABLE `t_user_profile` ADD COLUMN `tenant_id` bigint(20) UNSIGNED NOT NULL DEFAULT 1 COMMENT '租户ID';
ALTER TABLE `t_user_profile` ADD INDEX `idx_tenant_id`(`tenant_id`) USING BTREE;

-- t_user_behavior_event
ALTER TABLE `t_user_behavior_event` ADD COLUMN `tenant_id` bigint(20) UNSIGNED NOT NULL DEFAULT 1 COMMENT '租户ID';
ALTER TABLE `t_user_behavior_event` ADD INDEX `idx_tenant_id`(`tenant_id`) USING BTREE;

-- t_recommendation_config
ALTER TABLE `t_recommendation_config` ADD COLUMN `tenant_id` bigint(20) UNSIGNED NOT NULL DEFAULT 1 COMMENT '租户ID';
ALTER TABLE `t_recommendation_config` ADD INDEX `idx_tenant_id`(`tenant_id`) USING BTREE;

-- t_recommendation_log
ALTER TABLE `t_recommendation_log` ADD COLUMN `tenant_id` bigint(20) UNSIGNED NOT NULL DEFAULT 1 COMMENT '租户ID';
ALTER TABLE `t_recommendation_log` ADD INDEX `idx_tenant_id`(`tenant_id`) USING BTREE;

-- =====================================================
-- 4. 创建文件记录表
-- =====================================================
CREATE TABLE IF NOT EXISTS `t_file` (
    `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '文件ID',
    `original_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '原始文件名',
    `url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '文件访问URL',
    `file_size` bigint(20) UNSIGNED NULL DEFAULT 0 COMMENT '文件大小(字节)',
    `content_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '文件MIME类型',
    `tenant_id` bigint(20) UNSIGNED NOT NULL DEFAULT 1 COMMENT '租户ID',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `idx_tenant_id`(`tenant_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '文件记录表' ROW_FORMAT = DYNAMIC;

-- =====================================================
-- 5. 更新默认租户关联数据中 admin_user_id
-- =====================================================
UPDATE `t_tenant` SET `admin_user_id` = 1 WHERE `id` = 1;

-- =====================================================
-- 6. 添加超级管理员角色（用于管理所有租户）
-- =====================================================
INSERT INTO `t_role` (`name`, `code`, `description`, `sort`, `create_time`, `update_time`, `is_deleted`)
VALUES ('超级管理员', 'ROLE_SUPER_ADMIN', '可管理所有租户的系统级管理员', 0, NOW(), NOW(), 0);
