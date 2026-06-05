-- 投递记录增加职位字段
ALTER TABLE `t_resume_application`
    ADD COLUMN `position` varchar(100) NULL DEFAULT '' COMMENT '投递职位' AFTER `company`;

-- 用户表增加邮箱字段
ALTER TABLE `t_user`
    ADD COLUMN `email` varchar(100) NULL DEFAULT '' COMMENT '用户邮箱' AFTER `role_id`;
