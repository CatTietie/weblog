CREATE TABLE `t_resume_application` (
    `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT,
    `resume_id` bigint(20) UNSIGNED NOT NULL COMMENT '简历id',
    `user_id` bigint(20) UNSIGNED NOT NULL COMMENT '用户id',
    `company` varchar(100) NOT NULL COMMENT '公司名称',
    `apply_time` datetime NULL DEFAULT NULL COMMENT '投递时间',
    `channel` varchar(60) NULL DEFAULT '' COMMENT '投递渠道',
    `status` tinyint(2) NOT NULL DEFAULT 0 COMMENT '0-已投递 1-面试中 2-已录用 3-已拒绝 4-已放弃',
    `remark` varchar(500) NULL DEFAULT '' COMMENT '备注',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    INDEX `idx_resume_id`(`resume_id`),
    INDEX `idx_user_id`(`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='简历投递记录表';
