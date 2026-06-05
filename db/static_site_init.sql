-- 静态站点生成功能初始化 SQL

-- 静态站点配置表（单行）
CREATE TABLE `t_static_site_config` (
    `id` BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    `comment_provider` VARCHAR(20) DEFAULT 'none' COMMENT '评论提供商: none/giscus/waline',
    `comment_config` TEXT COMMENT 'JSON: 评论提供商配置参数',
    `github_enabled` TINYINT(1) DEFAULT 0 COMMENT '是否启用 GitHub Pages 部署',
    `github_token` VARCHAR(255) DEFAULT '' COMMENT 'GitHub Personal Access Token',
    `github_repo` VARCHAR(255) DEFAULT '' COMMENT '目标仓库 owner/repo',
    `github_branch` VARCHAR(100) DEFAULT 'gh-pages' COMMENT '目标分支',
    `github_cname` VARCHAR(255) DEFAULT '' COMMENT 'GitHub Pages 自定义域名',
    `oss_enabled` TINYINT(1) DEFAULT 0 COMMENT '是否启用阿里云 OSS 部署',
    `oss_endpoint` VARCHAR(255) DEFAULT '',
    `oss_access_key_id` VARCHAR(255) DEFAULT '',
    `oss_access_key_secret` VARCHAR(255) DEFAULT '',
    `oss_bucket_name` VARCHAR(255) DEFAULT '',
    `oss_base_path` VARCHAR(255) DEFAULT '' COMMENT 'OSS 中的前缀路径',
    `auto_deploy` TINYINT(1) DEFAULT 0 COMMENT '发布文章时自动部署',
    `output_base_url` VARCHAR(255) DEFAULT '/' COMMENT '静态站点的基础 URL',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='静态站点生成配置';

-- 插入默认配置
INSERT INTO `t_static_site_config` (`id`, `comment_provider`, `output_base_url`) VALUES (1, 'none', '/');

-- 静态生成任务日志表
CREATE TABLE `t_static_gen_task` (
    `id` BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    `task_type` VARCHAR(20) NOT NULL COMMENT 'FULL/INCREMENTAL',
    `status` VARCHAR(20) NOT NULL COMMENT 'PENDING/RUNNING/SUCCESS/FAILED',
    `deploy_target` VARCHAR(20) DEFAULT '' COMMENT 'GITHUB/OSS/LOCAL',
    `total_pages` INT DEFAULT 0 COMMENT '总页面数',
    `generated_pages` INT DEFAULT 0 COMMENT '已生成页面数',
    `error_message` TEXT COMMENT '错误信息',
    `output_path` VARCHAR(500) DEFAULT '' COMMENT '本地输出路径',
    `deployed_url` VARCHAR(500) DEFAULT '' COMMENT '部署后的访问 URL',
    `start_time` DATETIME COMMENT '开始时间',
    `end_time` DATETIME COMMENT '结束时间',
    `duration_ms` BIGINT DEFAULT 0 COMMENT '耗时(毫秒)',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='静态生成任务日志';

-- 已生成页面注册表（增量构建用）
CREATE TABLE `t_static_gen_page` (
    `id` BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    `page_type` VARCHAR(20) NOT NULL COMMENT 'ARTICLE/INDEX/CATEGORY/TAG/ARCHIVE',
    `ref_id` BIGINT DEFAULT NULL COMMENT '关联ID: article_id/category_id/tag_id',
    `page_number` INT DEFAULT 1 COMMENT '分页页码',
    `content_hash` VARCHAR(64) DEFAULT '' COMMENT '源数据 MD5 哈希',
    `output_path` VARCHAR(500) NOT NULL COMMENT '输出文件相对路径',
    `last_generated_time` DATETIME NOT NULL COMMENT '最后生成时间',
    `source_update_time` DATETIME COMMENT '源数据更新时间',
    INDEX `idx_page_type_ref` (`page_type`, `ref_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='已生成页面注册表';

-- 静态站点管理权限
INSERT INTO `t_permission` (`name`, `code`, `type`, `path`, `method`, `parent_id`, `sort`)
VALUES ('静态站点管理', 'static-site:manage', 1, '/admin/static-site/**', 'POST', 0, 100);
