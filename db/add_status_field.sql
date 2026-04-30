-- 为 t_user 表添加 status 字段
-- 执行前请确保已选择 weblog 数据库

USE weblog;

-- 添加 status 字段
ALTER TABLE `t_user` ADD COLUMN `status` tinyint(2) NOT NULL DEFAULT 0 COMMENT '状态：0-启用，1-禁用' AFTER `password`;

-- 为现有用户设置默认状态为启用
UPDATE t_user SET status = 0;

-- 验证字段是否添加成功
DESC t_user;
