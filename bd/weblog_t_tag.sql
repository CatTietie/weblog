create table t_tag
(
    is_deleted  tinyint(2)  default 0                 not null comment '逻辑删除标志位：0：未删除 1：已删除',
    update_time datetime    default CURRENT_TIMESTAMP not null comment '最后一次更新时间',
    create_time datetime    default CURRENT_TIMESTAMP not null comment '创建时间',
    name        varchar(60) default ''                not null comment '标签名称',
    id          bigint unsigned auto_increment comment '标签id'
        primary key,
    constraint uk_name
        unique (name)
)
    comment '文章标签表' charset = utf8mb4;

create index idx_create_time
    on t_tag (create_time);

INSERT INTO weblog.t_tag (id, name, create_time, update_time, is_deleted) VALUES (38, 'MySQL', '2024-07-13 17:08:54', '2024-07-13 17:08:54', 0);
INSERT INTO weblog.t_tag (id, name, create_time, update_time, is_deleted) VALUES (39, 'Docker', '2024-07-13 17:08:54', '2024-07-13 17:08:54', 0);
INSERT INTO weblog.t_tag (id, name, create_time, update_time, is_deleted) VALUES (40, 'Redis', '2024-07-13 17:08:54', '2024-07-13 17:08:54', 0);
INSERT INTO weblog.t_tag (id, name, create_time, update_time, is_deleted) VALUES (41, 'SpringBoot', '2024-07-13 17:08:54', '2024-07-13 17:08:54', 0);
INSERT INTO weblog.t_tag (id, name, create_time, update_time, is_deleted) VALUES (42, 'SSM', '2024-07-13 17:08:54', '2024-07-13 17:08:54', 0);
INSERT INTO weblog.t_tag (id, name, create_time, update_time, is_deleted) VALUES (43, 'Java', '2024-07-13 17:08:54', '2024-07-13 17:08:54', 0);
INSERT INTO weblog.t_tag (id, name, create_time, update_time, is_deleted) VALUES (44, 'JavaWeb', '2024-07-13 17:08:54', '2024-07-13 17:08:54', 0);
INSERT INTO weblog.t_tag (id, name, create_time, update_time, is_deleted) VALUES (45, 'ShardingShere', '2024-07-13 17:08:54', '2024-07-13 17:08:54', 0);
INSERT INTO weblog.t_tag (id, name, create_time, update_time, is_deleted) VALUES (46, '短链接', '2024-07-13 17:08:54', '2024-07-13 17:08:54', 0);