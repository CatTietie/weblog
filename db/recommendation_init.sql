-- 智能推荐模块建表脚本
-- 创建日期: 2026-06-03

-- 用户行为事件表
CREATE TABLE IF NOT EXISTS `t_user_behavior_event` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `event_type` TINYINT NOT NULL COMMENT '事件类型: 1-阅读 2-标签点击 3-搜索 4-简历浏览',
    `article_id` BIGINT DEFAULT NULL COMMENT '文章ID(阅读事件时)',
    `tag_id` BIGINT DEFAULT NULL COMMENT '标签ID(标签点击事件时)',
    `keyword` VARCHAR(100) DEFAULT NULL COMMENT '搜索关键词(搜索事件时)',
    `duration_seconds` INT DEFAULT 0 COMMENT '停留时长(秒,阅读事件时)',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '事件时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id_create_time` (`user_id`, `create_time`),
    KEY `idx_event_type` (`event_type`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户行为事件表';

-- 用户画像表
CREATE TABLE IF NOT EXISTS `t_user_profile` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `tag_weights` TEXT NOT NULL COMMENT 'JSON格式的标签权重数组: [{"tagId":1,"weight":0.85},...]',
    `last_calculated_time` DATETIME NOT NULL COMMENT '最后一次计算时间',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户画像表';

-- 推荐运营配置表
CREATE TABLE IF NOT EXISTS `t_recommendation_config` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
    `config_type` TINYINT NOT NULL COMMENT '配置类型: 1-置顶文章 2-标签屏蔽 3-文章权重调整',
    `article_id` BIGINT DEFAULT NULL COMMENT '文章ID',
    `tag_id` BIGINT DEFAULT NULL COMMENT '标签ID(屏蔽标签时)',
    `weight_adjustment` DECIMAL(5,2) DEFAULT 0.00 COMMENT '权重调整值',
    `is_active` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '是否生效: 0-否 1-是',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_config_type_active` (`config_type`, `is_active`),
    KEY `idx_article_id` (`article_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='推荐运营配置表';

-- 推荐点击日志表
CREATE TABLE IF NOT EXISTS `t_recommendation_log` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `article_id` BIGINT NOT NULL COMMENT '推荐的文章ID',
    `source` TINYINT NOT NULL COMMENT '推荐来源: 1-首页推荐 2-详情页相关推荐',
    `is_clicked` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否点击: 0-否 1-是',
    `recommend_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '推荐时间',
    `click_time` DATETIME DEFAULT NULL COMMENT '点击时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_recommend_time` (`user_id`, `recommend_time`),
    KEY `idx_recommend_time` (`recommend_time`),
    KEY `idx_article_id` (`article_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='推荐点击日志表';
