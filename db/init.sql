create table `weblog-cat`.t_article
(
    read_num    int unsigned default '1'               not null comment '被阅读次数',
    is_deleted  tinyint      default 0                 not null comment '删除标志位：0：未删除 1：已删除',
    update_time datetime     default CURRENT_TIMESTAMP not null comment '最后一次更新时间',
    create_time datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    summary     varchar(160) default ''                null comment '文章摘要',
    cover       varchar(120) default ''                not null comment '文章封面',
    title       varchar(120) default ''                not null comment '文章标题',
    id          bigint unsigned auto_increment comment '文章id'
        primary key
)
    comment '文章表';

create index idx_create_time
    on `weblog-cat`.t_article (create_time);

create table `weblog-cat`.t_article_category_rel
(
    category_id bigint unsigned not null comment '分类id',
    article_id  bigint unsigned not null comment '文章id',
    id          bigint unsigned auto_increment comment 'id'
        primary key,
    constraint uni_article_id
        unique (article_id)
)
    comment '文章所属分类关联表';

create index idx_category_id
    on `weblog-cat`.t_article_category_rel (category_id);

create table `weblog-cat`.t_article_content
(
    content    text   null comment '教程正文',
    article_id bigint not null comment '文章id',
    id         bigint unsigned auto_increment comment '文章内容id'
        primary key
)
    comment '文章内容表';

create index idx_article_id
    on `weblog-cat`.t_article_content (article_id);

create table `weblog-cat`.t_article_tag_rel
(
    tag_id     bigint unsigned not null comment '标签id',
    article_id bigint unsigned not null comment '文章id',
    id         bigint unsigned auto_increment comment 'id'
        primary key
)
    comment '文章对应标签关联表';

create index idx_article_id
    on `weblog-cat`.t_article_tag_rel (article_id);

create index idx_tag_id
    on `weblog-cat`.t_article_tag_rel (tag_id);

create table `weblog-cat`.t_article_update_history
(
    article_id  bigint unsigned not null comment '关联文章表的 id',
    update_time datetime        not null comment '每次更新文章的时间',
    primary key (article_id, update_time)
)
    comment '文章更新历史表';

create table `weblog-cat`.t_blog_settings
(
    zhihu_homepage  varchar(60)  default '' not null comment '知乎主页访问地址',
    gitee_homepage  varchar(60)  default '' not null comment 'Gitee 主页访问地址',
    csdn_homepage   varchar(60)  default '' not null comment 'CSDN 主页访问地址',
    github_homepage varchar(60)  default '' not null comment 'GitHub 主页访问地址',
    avatar          varchar(120) default '' not null comment '作者头像',
    introduction    varchar(120) default '' not null comment '介绍语',
    author          varchar(20)  default '' not null comment '作者名',
    name            varchar(60)  default '' not null comment '博客名称',
    logo            varchar(120) default '' not null comment '博客Logo',
    id              bigint unsigned auto_increment comment 'id'
        primary key,
    username        varchar(30)             not null,
    constraint t_blog_settings_username_uindex
        unique (username)
)
    comment '博客设置表';

create table `weblog-cat`.t_category
(
    is_deleted  tinyint     default 0                 not null comment '逻辑删除标志位：0：未删除 1：已删除',
    update_time datetime    default CURRENT_TIMESTAMP not null comment '最后一次更新时间',
    create_time datetime    default CURRENT_TIMESTAMP not null comment '创建时间',
    name        varchar(60) default ''                not null comment '分类名称',
    id          bigint unsigned auto_increment comment '分类id'
        primary key,
    constraint uk_name
        unique (name)
)
    comment '文章分类表';

create index idx_create_time
    on `weblog-cat`.t_category (create_time);

create table `weblog-cat`.t_statistics_article_pv
(
    update_time datetime default CURRENT_TIMESTAMP not null comment '最后一次更新时间',
    create_time datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    pv_count    bigint unsigned                    not null comment 'pv浏览量',
    pv_date     date                               not null comment '被统计的日期',
    id          bigint unsigned auto_increment comment 'id'
        primary key,
    constraint uk_pv_date
        unique (pv_date)
)
    comment '统计表 - 文章 PV (浏览量)';

create table `weblog-cat`.t_tag
(
    is_deleted  tinyint     default 0                 not null comment '逻辑删除标志位：0：未删除 1：已删除',
    update_time datetime    default CURRENT_TIMESTAMP not null comment '最后一次更新时间',
    create_time datetime    default CURRENT_TIMESTAMP not null comment '创建时间',
    name        varchar(60) default ''                not null comment '标签名称',
    id          bigint unsigned auto_increment comment '标签id'
        primary key,
    constraint uk_name
        unique (name)
)
    comment '文章标签表';

create index idx_create_time
    on `weblog-cat`.t_tag (create_time);

create table `weblog-cat`.t_user
(
    is_deleted      tinyint     default 0                 not null comment '删除标志位：0：未删除 1：已删除',
    update_time     datetime    default CURRENT_TIMESTAMP not null comment '最后一次更新时间',
    create_time     datetime    default CURRENT_TIMESTAMP not null comment '创建时间',
    password        varchar(60)                           not null comment '密码',
    username        varchar(60)                           not null comment '用户名',
    id              bigint unsigned auto_increment comment 'id'
        primary key,
    user_type       varchar(20) default 'NORMAL'          not null comment '用户类型（ADMIN/NORMAL）',
    social_accounts json                                  null comment '第三方登录信息'
)
    comment '用户表';

create index idx_create_time
    on `weblog-cat`.t_user (create_time);

create index idx_username
    on `weblog-cat`.t_user (username);

create table `weblog-cat`.t_user_role
(
    create_time datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    role        varchar(60)                        not null comment '角色',
    username    varchar(60)                        not null comment '用户名',
    id          bigint unsigned auto_increment comment 'id'
        primary key
)
    comment '用户角色表';

create index idx_username
    on `weblog-cat`.t_user_role (username);

create table `weblog-cat`.user_visit_stats
(
    id             int auto_increment
        primary key,
    visit_time     datetime             not null,
    user_ip        varchar(45)          not null,
    device_type    varchar(50)          null,
    browser_name   varchar(50)          null,
    os_name        varchar(50)          null,
    is_new_visitor tinyint(1) default 0 null,
    page_url       varchar(255)         null
);

