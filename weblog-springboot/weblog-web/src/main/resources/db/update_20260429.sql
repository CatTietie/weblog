-- 多用户功能更新脚本
-- 执行日期：2026-04-29

-- 1. 为 t_user 表添加 status 字段（状态：0-启用，1-禁用）
ALTER TABLE `t_user` ADD COLUMN `status` tinyint(2) NOT NULL DEFAULT 0 COMMENT '状态：0-启用，1-禁用' AFTER `password`;

-- 2. 为现有用户设置默认状态为启用
-- 注意：如果数据库中已有用户，默认都会被设置为启用状态

-- 3. 可选：为 status 字段添加索引（根据性能需求决定是否添加）
-- ALTER TABLE `t_user` ADD INDEX `idx_status`(`status`) USING BTREE;
