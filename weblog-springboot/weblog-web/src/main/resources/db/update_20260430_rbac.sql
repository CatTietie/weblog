-- RBAC 模型初始化脚本
-- 包含角色表、权限表、角色-权限关联表
-- 以及用户表添加 role_id 字段并迁移现有数据

-- 1. 创建角色表
DROP TABLE IF EXISTS `t_role`;
CREATE TABLE `t_role` (
    `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
    `name` varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '角色名称',
    `code` varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '角色编码',
    `description` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '角色描述',
    `sort` int(11) NOT NULL DEFAULT 0 COMMENT '排序',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '最后一次更新时间',
    `is_deleted` tinyint(2) NOT NULL DEFAULT 0 COMMENT '删除标志位：0：未删除 1：已删除',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `uk_code`(`code`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '角色表' ROW_FORMAT = DYNAMIC;

-- 2. 创建权限表
DROP TABLE IF EXISTS `t_permission`;
CREATE TABLE `t_permission` (
    `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
    `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '权限名称',
    `code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '权限编码',
    `type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'menu' COMMENT '类型：menu-菜单，button-按钮，api-接口',
    `path` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '路径（菜单路径或接口路径）',
    `method` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '请求方法（GET/POST/PUT/DELETE）',
    `parent_id` bigint(20) UNSIGNED NULL DEFAULT 0 COMMENT '父级ID',
    `sort` int(11) NOT NULL DEFAULT 0 COMMENT '排序',
    `description` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '权限描述',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '最后一次更新时间',
    `is_deleted` tinyint(2) NOT NULL DEFAULT 0 COMMENT '删除标志位：0：未删除 1：已删除',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `uk_code`(`code`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '权限表' ROW_FORMAT = DYNAMIC;

-- 3. 创建角色-权限关联表
DROP TABLE IF EXISTS `t_role_permission`;
CREATE TABLE `t_role_permission` (
    `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
    `role_id` bigint(20) UNSIGNED NOT NULL COMMENT '角色ID',
    `permission_id` bigint(20) UNSIGNED NOT NULL COMMENT '权限ID',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `idx_role_id`(`role_id`) USING BTREE,
    INDEX `idx_permission_id`(`permission_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '角色-权限关联表' ROW_FORMAT = DYNAMIC;

-- 4. 为用户表添加 role_id 字段
ALTER TABLE `t_user` ADD COLUMN `role_id` bigint(20) UNSIGNED NULL DEFAULT NULL COMMENT '角色ID' AFTER `status`;

-- 5. 初始化角色数据
INSERT INTO `t_role` (`id`, `name`, `code`, `description`, `sort`, `create_time`, `update_time`, `is_deleted`) VALUES 
(1, '管理员', 'ROLE_ADMIN', '系统管理员，拥有所有权限', 1, NOW(), NOW(), 0),
(2, '普通用户', 'ROLE_USER', '普通用户，拥有部分权限', 2, NOW(), NOW(), 0);

-- 6. 初始化权限数据
INSERT INTO `t_permission` (`id`, `name`, `code`, `type`, `path`, `method`, `parent_id`, `sort`, `description`, `create_time`, `update_time`, `is_deleted`) VALUES 
(1, '仪表盘', 'dashboard', 'menu', '/admin/index', '', 0, 1, '仪表盘菜单', NOW(), NOW(), 0),
(2, '文章管理', 'article', 'menu', '/admin/article/list', '', 0, 2, '文章管理菜单', NOW(), NOW(), 0),
(3, '分类管理', 'category', 'menu', '/admin/category/list', '', 0, 3, '分类管理菜单', NOW(), NOW(), 0),
(4, '标签管理', 'tag', 'menu', '/admin/tag/list', '', 0, 4, '标签管理菜单', NOW(), NOW(), 0),
(5, '用户管理', 'user', 'menu', '/admin/user/list', '', 0, 5, '用户管理菜单', NOW(), NOW(), 0),
(6, '博客设置', 'blogsettings', 'menu', '/admin/blog/settings', '', 0, 6, '博客设置菜单', NOW(), NOW(), 0),
(7, '用户统计', 'userStatistics', 'menu', '/admin/user/statistics', '', 0, 7, '用户统计菜单', NOW(), NOW(), 0),

-- 接口权限
(100, '创建用户', 'user:create', 'api', '/admin/users', 'POST', 5, 1, '创建用户接口', NOW(), NOW(), 0),
(101, '查询用户列表', 'user:list', 'api', '/admin/users/list', 'POST', 5, 2, '查询用户列表接口', NOW(), NOW(), 0),
(102, '查询角色列表', 'role:list', 'api', '/admin/role/select/list', 'POST', 0, 8, '查询角色列表接口', NOW(), NOW(), 0);

-- 7. 初始化角色-权限关联（管理员拥有所有权限）
INSERT INTO `t_role_permission` (`role_id`, `permission_id`, `create_time`) VALUES 
(1, 1, NOW()),
(1, 2, NOW()),
(1, 3, NOW()),
(1, 4, NOW()),
(1, 5, NOW()),
(1, 6, NOW()),
(1, 7, NOW()),
(1, 100, NOW()),
(1, 101, NOW()),
(1, 102, NOW());

-- 普通用户权限（没有用户管理权限）
INSERT INTO `t_role_permission` (`role_id`, `permission_id`, `create_time`) VALUES 
(2, 1, NOW()),
(2, 2, NOW()),
(2, 3, NOW()),
(2, 4, NOW()),
(2, 6, NOW());

-- 8. 迁移现有用户数据：根据 t_user_role 表中的 role 字段设置 role_id
-- ROLE_ADMIN -> role_id = 1
-- 其他 -> role_id = 2

UPDATE `t_user` u
INNER JOIN `t_user_role` ur ON u.username = ur.username
SET u.role_id = CASE 
    WHEN ur.role = 'ROLE_ADMIN' THEN 1
    ELSE 2
END
WHERE u.role_id IS NULL;

-- 如果用户没有在 t_user_role 表中，默认设置为普通用户
UPDATE `t_user` SET role_id = 2 WHERE role_id IS NULL;
