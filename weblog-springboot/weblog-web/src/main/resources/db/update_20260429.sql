-- 多用户功能更新脚本
-- 执行日期：2026-04-29

-- 1. 为 t_user 表添加 status 字段（状态：0-启用，1-禁用）
-- 先检查字段是否存在，避免重复添加
SET @dbname = DATABASE();
SET @tablename = 't_user';
SET @columnname = 'status';
SET @preparedStatement = (SELECT IF(
  (
    SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
    WHERE
      (table_name = @tablename)
      AND (table_schema = @dbname)
      AND (column_name = @columnname)
  ) > 0,
  'SELECT 1',
  CONCAT('ALTER TABLE ', @tablename, ' ADD COLUMN ', @columnname, ' tinyint(2) NOT NULL DEFAULT 0 COMMENT ''状态：0-启用，1-禁用'' AFTER password')
));
PREPARE alterIfNotExists FROM @preparedStatement;
EXECUTE alterIfNotExists;
DEALLOCATE PREPARE alterIfNotExists;

-- 2. 为现有用户设置默认状态为启用
UPDATE t_user SET status = 0 WHERE status IS NULL;

-- 3. 可选：为 status 字段添加索引（根据性能需求决定是否添加）
-- ALTER TABLE `t_user` ADD INDEX `idx_status`(`status`) USING BTREE;
