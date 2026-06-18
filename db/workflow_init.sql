-- 工作流自动化引擎 DDL
-- 创建时间: 2026-06-04

-- 工作流定义表
CREATE TABLE IF NOT EXISTS `t_workflow` (
    `id`              BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `name`            VARCHAR(100) NOT NULL COMMENT '工作流名称',
    `description`     VARCHAR(500) DEFAULT '' COMMENT '工作流描述',
    `trigger_type`    VARCHAR(50) NOT NULL COMMENT '触发类型: ARTICLE_PUBLISHED, COMMENT_RECEIVED, USER_REGISTERED',
    `definition_json` TEXT NOT NULL COMMENT '工作流节点定义JSON',
    `is_enabled`      TINYINT(1) NOT NULL DEFAULT 1 COMMENT '是否启用: 0=禁用, 1=启用',
    `is_deleted`      TINYINT(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0=未删除, 1=已删除',
    `create_time`     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time`     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX `idx_trigger_type` (`trigger_type`),
    INDEX `idx_enabled` (`is_enabled`, `is_deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='工作流定义表';

-- 工作流执行实例表
CREATE TABLE IF NOT EXISTS `t_workflow_execution` (
    `id`              BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `workflow_id`     BIGINT UNSIGNED NOT NULL COMMENT '工作流ID',
    `status`          VARCHAR(20) NOT NULL DEFAULT 'RUNNING' COMMENT '执行状态: RUNNING, WAITING_DELAY, COMPLETED, FAILED',
    `trigger_data`    TEXT NOT NULL COMMENT '触发时的事件数据快照JSON',
    `current_node_id` VARCHAR(100) DEFAULT NULL COMMENT '当前执行到的节点ID',
    `context_json`    TEXT DEFAULT NULL COMMENT '执行上下文JSON',
    `delay_until`     DATETIME DEFAULT NULL COMMENT '延迟等待截止时间',
    `error_message`   VARCHAR(500) DEFAULT NULL COMMENT '错误信息',
    `is_debug`        TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否为调试模式执行',
    `start_time`      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `end_time`        DATETIME DEFAULT NULL,
    INDEX `idx_workflow_id` (`workflow_id`),
    INDEX `idx_status_delay` (`status`, `delay_until`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='工作流执行实例表';

-- 工作流节点执行日志表
CREATE TABLE IF NOT EXISTS `t_workflow_execution_log` (
    `id`              BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `execution_id`    BIGINT UNSIGNED NOT NULL COMMENT '执行实例ID',
    `node_id`         VARCHAR(100) NOT NULL COMMENT '节点ID',
    `node_type`       VARCHAR(50) NOT NULL COMMENT '节点类型',
    `node_label`      VARCHAR(200) DEFAULT NULL COMMENT '节点名称',
    `status`          VARCHAR(20) NOT NULL COMMENT '节点状态: SUCCESS, FAILED, SKIPPED',
    `input_data`      TEXT DEFAULT NULL COMMENT '输入数据JSON',
    `output_data`     TEXT DEFAULT NULL COMMENT '输出数据JSON',
    `error_message`   VARCHAR(500) DEFAULT NULL,
    `execute_time`    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '执行时间',
    `duration_ms`     BIGINT DEFAULT NULL COMMENT '执行耗时(毫秒)',
    INDEX `idx_execution_id` (`execution_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='工作流节点执行日志表';

-- 权限：工作流管理（菜单级，type='menu'）
INSERT IGNORE INTO `t_permission` (`name`, `code`, `type`, `path`, `parent_id`, `sort`, `description`, `is_deleted`)
VALUES ('工作流管理', 'workflow', 'menu', '/admin/workflow/list', 0, 100, '工作流自动化菜单', 0);

-- 权限：工作流操作（接口级，type='api'，parent_id 指向菜单权限）
SET @workflow_menu_id = (SELECT `id` FROM `t_permission` WHERE `code` = 'workflow' LIMIT 1);

INSERT IGNORE INTO `t_permission` (`name`, `code`, `type`, `parent_id`, `sort`, `description`, `is_deleted`)
VALUES ('工作流操作', 'workflow:manage', 'api', @workflow_menu_id, 1, '工作流管理接口', 0);

-- 关联菜单权限 'workflow' 到 ADMIN 角色 (role_id = 1)
INSERT INTO `t_role_permission` (`role_id`, `permission_id`)
SELECT 1, `id` FROM `t_permission` WHERE `code` = 'workflow'
AND NOT EXISTS (
    SELECT 1 FROM `t_role_permission` rp
    WHERE rp.role_id = 1 AND rp.permission_id = (SELECT `id` FROM `t_permission` WHERE `code` = 'workflow')
);

-- 关联接口权限 'workflow:manage' 到 ADMIN 角色 (role_id = 1)
INSERT INTO `t_role_permission` (`role_id`, `permission_id`)
SELECT 1, `id` FROM `t_permission` WHERE `code` = 'workflow:manage'
AND NOT EXISTS (
    SELECT 1 FROM `t_role_permission` rp
    WHERE rp.role_id = 1 AND rp.permission_id = (SELECT `id` FROM `t_permission` WHERE `code` = 'workflow:manage')
);
