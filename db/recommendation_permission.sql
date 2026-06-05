-- 推荐管理权限迁移脚本
-- 创建日期: 2026-06-03

-- 添加推荐管理菜单权限
INSERT INTO `t_permission` (`id`, `name`, `code`, `type`, `path`, `method`, `parent_id`, `sort`, `description`, `create_time`, `update_time`, `is_deleted`) VALUES
(12, '推荐管理', 'recommendation', 'menu', '/admin/recommendation/dashboard', '', 0, 12, '推荐管理菜单', NOW(), NOW(), 0);

-- 添加推荐管理子权限
INSERT INTO `t_permission` (`id`, `name`, `code`, `type`, `path`, `method`, `parent_id`, `sort`, `description`, `create_time`, `update_time`, `is_deleted`) VALUES
(1101, '推荐看板', 'recommendation:dashboard', 'button', '/admin/recommendation/dashboard', 'POST', 12, 1, '查看推荐效果看板', NOW(), NOW(), 0),
(1102, '推荐配置管理', 'recommendation:config', 'button', '/admin/recommendation/config', 'POST', 12, 2, '管理推荐运营配置', NOW(), NOW(), 0),
(1103, '用户画像管理', 'recommendation:profile', 'button', '/admin/recommendation/profile', 'POST', 12, 3, '查看和修正用户画像', NOW(), NOW(), 0);

-- 为 ROLE_ADMIN (id=1) 分配推荐管理权限
INSERT INTO `t_role_permission` (`role_id`, `permission_id`, `create_time`) VALUES
(1, 12, NOW()),
(1, 1101, NOW()),
(1, 1102, NOW()),
(1, 1103, NOW());
