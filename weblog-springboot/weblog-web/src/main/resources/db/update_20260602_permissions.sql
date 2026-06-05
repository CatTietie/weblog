-- 权限细化迁移脚本
-- 重新填充 t_permission 表为完整的树形权限数据
-- 并更新角色-权限关联

-- ============================================
-- 1. 清空旧数据
-- ============================================
DELETE FROM `t_role_permission`;
DELETE FROM `t_permission`;

-- ============================================
-- 2. 插入菜单级权限（parent_id = 0）
-- ============================================
INSERT INTO `t_permission` (`id`, `name`, `code`, `type`, `path`, `method`, `parent_id`, `sort`, `description`, `create_time`, `update_time`, `is_deleted`) VALUES
(1, '仪表盘', 'dashboard', 'menu', '/admin/index', '', 0, 1, '仪表盘菜单', NOW(), NOW(), 0),
(2, '文章管理', 'article', 'menu', '/admin/article/list', '', 0, 2, '文章管理菜单', NOW(), NOW(), 0),
(3, '分类管理', 'category', 'menu', '/admin/category/list', '', 0, 3, '分类管理菜单', NOW(), NOW(), 0),
(4, '标签管理', 'tag', 'menu', '/admin/tag/list', '', 0, 4, '标签管理菜单', NOW(), NOW(), 0),
(5, '用户管理', 'user', 'menu', '/admin/user/list', '', 0, 5, '用户管理菜单', NOW(), NOW(), 0),
(6, '博客设置', 'blogsettings', 'menu', '/admin/blog/settings', '', 0, 6, '博客设置菜单', NOW(), NOW(), 0),
(7, '评论管理', 'comment', 'menu', '/admin/comment/list', '', 0, 7, '评论管理菜单', NOW(), NOW(), 0),
(8, '通知管理', 'notification', 'menu', '/admin/notification/send', '', 0, 8, '通知管理菜单', NOW(), NOW(), 0),
(9, '角色管理', 'role', 'menu', '/admin/role/list', '', 0, 9, '角色管理菜单', NOW(), NOW(), 0),
(10, '简历模板管理', 'template', 'menu', '/admin/resume-template/list', '', 0, 10, '简历模板管理菜单', NOW(), NOW(), 0),
(11, '文件管理', 'file', 'menu', '', '', 0, 11, '文件管理', NOW(), NOW(), 0);

-- ============================================
-- 3. 插入按钮/API级权限（parent_id 指向菜单）
-- ============================================

-- 文章权限 (parent_id = 2)
INSERT INTO `t_permission` (`id`, `name`, `code`, `type`, `path`, `method`, `parent_id`, `sort`, `description`, `create_time`, `update_time`, `is_deleted`) VALUES
(101, '发布文章', 'article:publish', 'button', '/admin/article/publish', 'POST', 2, 1, '发布文章', NOW(), NOW(), 0),
(102, '删除文章', 'article:delete', 'button', '/admin/article/delete', 'POST', 2, 2, '删除文章', NOW(), NOW(), 0),
(103, '编辑文章', 'article:update', 'button', '/admin/article/update', 'POST', 2, 3, '编辑文章', NOW(), NOW(), 0);

-- 分类权限 (parent_id = 3)
INSERT INTO `t_permission` (`id`, `name`, `code`, `type`, `path`, `method`, `parent_id`, `sort`, `description`, `create_time`, `update_time`, `is_deleted`) VALUES
(201, '新增分类', 'category:create', 'button', '/admin/category/add', 'POST', 3, 1, '新增分类', NOW(), NOW(), 0),
(202, '删除分类', 'category:delete', 'button', '/admin/category/delete', 'POST', 3, 2, '删除分类', NOW(), NOW(), 0);

-- 标签权限 (parent_id = 4)
INSERT INTO `t_permission` (`id`, `name`, `code`, `type`, `path`, `method`, `parent_id`, `sort`, `description`, `create_time`, `update_time`, `is_deleted`) VALUES
(301, '新增标签', 'tag:create', 'button', '/admin/tag/add', 'POST', 4, 1, '新增标签', NOW(), NOW(), 0),
(302, '删除标签', 'tag:delete', 'button', '/admin/tag/delete', 'POST', 4, 2, '删除标签', NOW(), NOW(), 0);

-- 用户权限 (parent_id = 5)
INSERT INTO `t_permission` (`id`, `name`, `code`, `type`, `path`, `method`, `parent_id`, `sort`, `description`, `create_time`, `update_time`, `is_deleted`) VALUES
(401, '创建用户', 'user:create', 'button', '/admin/users', 'POST', 5, 1, '创建用户', NOW(), NOW(), 0),
(402, '用户列表', 'user:list', 'button', '/admin/users/list', 'POST', 5, 2, '查看用户列表', NOW(), NOW(), 0),
(403, '查看用户', 'user:view', 'button', '/admin/users/{id}', 'GET', 5, 3, '查看用户详情', NOW(), NOW(), 0),
(404, '编辑用户', 'user:update', 'button', '/admin/users/{id}', 'PUT', 5, 4, '编辑用户', NOW(), NOW(), 0),
(405, '删除用户', 'user:delete', 'button', '/admin/users/{id}', 'DELETE', 5, 5, '删除用户', NOW(), NOW(), 0),
(406, '修改密码', 'user:password', 'button', '/admin/password/update', 'POST', 5, 6, '修改/重置密码', NOW(), NOW(), 0);

-- 博客设置权限 (parent_id = 6)
INSERT INTO `t_permission` (`id`, `name`, `code`, `type`, `path`, `method`, `parent_id`, `sort`, `description`, `create_time`, `update_time`, `is_deleted`) VALUES
(501, '更新设置', 'settings:update', 'button', '/admin/blog/settings/update', 'POST', 6, 1, '更新博客设置', NOW(), NOW(), 0);

-- 评论权限 (parent_id = 7)
INSERT INTO `t_permission` (`id`, `name`, `code`, `type`, `path`, `method`, `parent_id`, `sort`, `description`, `create_time`, `update_time`, `is_deleted`) VALUES
(601, '评论列表', 'comment:list', 'button', '/admin/comment/list', 'POST', 7, 1, '查看评论列表', NOW(), NOW(), 0),
(602, '删除评论', 'comment:delete', 'button', '/admin/comment/delete', 'POST', 7, 2, '删除评论', NOW(), NOW(), 0),
(603, '审核评论', 'comment:update', 'button', '/admin/comment/status/batch-update', 'POST', 7, 3, '批量审核评论', NOW(), NOW(), 0);

-- 通知权限 (parent_id = 8)
INSERT INTO `t_permission` (`id`, `name`, `code`, `type`, `path`, `method`, `parent_id`, `sort`, `description`, `create_time`, `update_time`, `is_deleted`) VALUES
(701, '发送通知', 'notification:send', 'button', '/admin/notification/sendGlobal', 'POST', 8, 1, '发送全局通知', NOW(), NOW(), 0);

-- 角色权限 (parent_id = 9)
INSERT INTO `t_permission` (`id`, `name`, `code`, `type`, `path`, `method`, `parent_id`, `sort`, `description`, `create_time`, `update_time`, `is_deleted`) VALUES
(801, '角色管理', 'role:manage', 'button', '/admin/roles', '', 9, 1, '角色与权限管理', NOW(), NOW(), 0);

-- 简历模板权限 (parent_id = 10)
INSERT INTO `t_permission` (`id`, `name`, `code`, `type`, `path`, `method`, `parent_id`, `sort`, `description`, `create_time`, `update_time`, `is_deleted`) VALUES
(901, '新增模板', 'template:create', 'button', '/admin/resume/template/add', 'POST', 10, 1, '新增简历模板', NOW(), NOW(), 0),
(902, '编辑模板', 'template:update', 'button', '/admin/resume/template/update', 'POST', 10, 2, '编辑简历模板', NOW(), NOW(), 0),
(903, '删除模板', 'template:delete', 'button', '/admin/resume/template/delete', 'POST', 10, 3, '删除简历模板', NOW(), NOW(), 0);

-- 文件权限 (parent_id = 11)
INSERT INTO `t_permission` (`id`, `name`, `code`, `type`, `path`, `method`, `parent_id`, `sort`, `description`, `create_time`, `update_time`, `is_deleted`) VALUES
(1001, '上传文件', 'file:upload', 'button', '/admin/file/upload', 'POST', 11, 1, '上传文件', NOW(), NOW(), 0);

-- ============================================
-- 4. 为 ROLE_ADMIN (id=1) 分配所有权限
-- ============================================
INSERT INTO `t_role_permission` (`role_id`, `permission_id`, `create_time`) VALUES
-- 菜单
(1, 1, NOW()), (1, 2, NOW()), (1, 3, NOW()), (1, 4, NOW()), (1, 5, NOW()),
(1, 6, NOW()), (1, 7, NOW()), (1, 8, NOW()), (1, 9, NOW()), (1, 10, NOW()), (1, 11, NOW()),
-- 按钮/API
(1, 101, NOW()), (1, 102, NOW()), (1, 103, NOW()),
(1, 201, NOW()), (1, 202, NOW()),
(1, 301, NOW()), (1, 302, NOW()),
(1, 401, NOW()), (1, 402, NOW()), (1, 403, NOW()), (1, 404, NOW()), (1, 405, NOW()), (1, 406, NOW()),
(1, 501, NOW()),
(1, 601, NOW()), (1, 602, NOW()), (1, 603, NOW()),
(1, 701, NOW()),
(1, 801, NOW()),
(1, 901, NOW()), (1, 902, NOW()), (1, 903, NOW()),
(1, 1001, NOW());

-- ============================================
-- 5. 为 ROLE_USER (id=2) 分配部分权限
-- ============================================
INSERT INTO `t_role_permission` (`role_id`, `permission_id`, `create_time`) VALUES
-- 菜单：仪表盘、文章、分类、标签、博客设置、文件
(2, 1, NOW()), (2, 2, NOW()), (2, 3, NOW()), (2, 4, NOW()), (2, 6, NOW()), (2, 11, NOW()),
-- 按钮：文章全部、分类全部、标签全部、设置更新、文件上传
(2, 101, NOW()), (2, 102, NOW()), (2, 103, NOW()),
(2, 201, NOW()), (2, 202, NOW()),
(2, 301, NOW()), (2, 302, NOW()),
(2, 501, NOW()),
(2, 1001, NOW());

-- ============================================
-- 6. 为 ROLE_VISITOR (id=3) 分配最少权限
-- ============================================
INSERT INTO `t_role_permission` (`role_id`, `permission_id`, `create_time`) VALUES
(3, 1, NOW());
