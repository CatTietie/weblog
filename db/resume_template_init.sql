CREATE TABLE `t_resume_template` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(50) NOT NULL COMMENT '模板名称',
  `component_name` VARCHAR(30) NOT NULL COMMENT '前端组件标识',
  `thumbnail` VARCHAR(500) DEFAULT '' COMMENT '缩略图URL',
  `description` VARCHAR(200) DEFAULT '' COMMENT '布局描述',
  `status` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '0-禁用 1-启用',
  `sort_order` INT NOT NULL DEFAULT 0 COMMENT '排序(小优先)',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_component_name` (`component_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='简历模板表';

INSERT INTO `t_resume_template` (`name`, `component_name`, `description`, `status`, `sort_order`) VALUES
('经典左右分栏', 'default', '左侧深色侧边栏 + 右侧内容区', 1, 1),
('现代全宽色块', 'modern', '顶部渐变横幅 + 卡片式内容区', 1, 2),
('简洁单栏', 'simple', '居中单栏极简排版', 1, 3);
