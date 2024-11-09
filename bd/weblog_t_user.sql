create table t_user
(
    is_deleted  tinyint(2) default 0                 not null comment '删除标志位：0：未删除 1：已删除',
    update_time datetime   default CURRENT_TIMESTAMP not null comment '最后一次更新时间',
    create_time datetime   default CURRENT_TIMESTAMP not null comment '创建时间',
    password    varchar(60)                          not null comment '密码',
    username    varchar(60)                          not null comment '用户名',
    id          bigint unsigned auto_increment comment 'id'
        primary key
)
    comment '用户表' charset = utf8mb4;

create index idx_create_time
    on t_user (create_time);

create index idx_username
    on t_user (username);

INSERT INTO weblog.t_user (id, username, password, create_time, update_time, is_deleted) VALUES (1, 'admin', '$2a$10$QAgvdcc0EIX0rojUL08kmuu0mpdW1BCe/OoF72XhipiQIfeMi.d5y', '2023-07-03 11:57:18', '2024-11-05 17:45:06', 0);
INSERT INTO weblog.t_user (id, username, password, create_time, update_time, is_deleted) VALUES (2, 'test', '$2a$10$L6ce4rQsyJ1k7ZCOfN6X4e5dHLyvg2X0t9JFEZBezDq0lds79Pxja', '2023-07-07 01:22:05', '2023-07-07 01:22:05', 0);