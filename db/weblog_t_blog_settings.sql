create table t_blog_settings
(
    id              bigint unsigned auto_increment comment 'id'
        primary key,
    logo            varchar(120) default '' not null comment '博客Logo',
    name            varchar(60)  default '' not null comment '博客名称',
    author          varchar(20)  default '' not null comment '作者名',
    introduction    varchar(120) default '' not null comment '介绍语',
    avatar          varchar(120) default '' not null comment '作者头像',
    github_homepage varchar(60)  default '' not null comment 'GitHub 主页访问地址',
    csdn_homepage   varchar(60)  default '' not null comment 'CSDN 主页访问地址',
    gitee_homepage  varchar(60)  default '' not null comment 'Gitee 主页访问地址',
    zhihu_homepage  varchar(60)  default '' not null comment '知乎主页访问地址'
)
    comment '博客设置表' charset = utf8mb4;

INSERT INTO weblog.t_blog_settings (id, logo, name, author, introduction, avatar, github_homepage, csdn_homepage, gitee_homepage, zhihu_homepage) VALUES (1, 'http://8.140.253.113:9000/weblog/317fde055317492ebc928aff1fe04c74.jpg', '猫贴贴', '猫贴贴', '奋斗是青春的主旋律', 'http://8.140.253.113:9000/weblog/60fadd34fb854990b7f99d72692d1cbe.png', 'https://www.yuque.com/dashboard', '', 'https://gitee.com/cat-tietie', 'https://www.quanxiaoha.com');