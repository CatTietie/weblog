create table t_category
(
    is_deleted  tinyint(2)  default 0                 not null comment '逻辑删除标志位：0：未删除 1：已删除',
    update_time datetime    default CURRENT_TIMESTAMP not null comment '最后一次更新时间',
    create_time datetime    default CURRENT_TIMESTAMP not null comment '创建时间',
    name        varchar(60) default ''                not null comment '分类名称',
    id          bigint unsigned auto_increment comment '分类id'
        primary key,
    constraint uk_name
        unique (name)
)
    comment '文章分类表' charset = utf8mb4;

create index idx_create_time
    on t_category (create_time);

INSERT INTO weblog.t_category (id, name, create_time, update_time, is_deleted) VALUES (20, '前端', '2024-07-13 09:09:19', '2024-07-13 09:09:19', 0);
INSERT INTO weblog.t_category (id, name, create_time, update_time, is_deleted) VALUES (21, '后端', '2024-07-13 09:09:23', '2024-07-13 09:09:23', 0);
INSERT INTO weblog.t_category (id, name, create_time, update_time, is_deleted) VALUES (22, 'Java', '2024-07-13 09:09:46', '2024-07-13 09:09:46', 0);
INSERT INTO weblog.t_category (id, name, create_time, update_time, is_deleted) VALUES (23, '微服务', '2024-07-13 09:09:55', '2024-07-13 09:09:55', 0);
INSERT INTO weblog.t_category (id, name, create_time, update_time, is_deleted) VALUES (24, '数据库', '2024-07-13 09:10:03', '2024-07-13 09:10:03', 0);
INSERT INTO weblog.t_category (id, name, create_time, update_time, is_deleted) VALUES (25, '框架', '2024-07-13 09:10:13', '2024-07-13 09:10:13', 0);