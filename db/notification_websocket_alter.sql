-- 通知表增加sender_id字段，用于记录全局通知的发送者
ALTER TABLE t_notification ADD COLUMN sender_id BIGINT DEFAULT NULL COMMENT '发送者ID(管理员发送全局通知时)';
