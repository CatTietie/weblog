create table t_user_role
(
    create_time datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    role        varchar(60)                        not null comment '角色',
    username    varchar(60)                        not null comment '用户名',
    id          bigint unsigned auto_increment comment 'id'
        primary key
)
    comment '用户角色表' charset = utf8mb4;

create index idx_username
    on t_user_role (username);

INSERT INTO weblog.t_user_role (id, username, role, create_time) VALUES (1, 'admin', 'ROLE_ADMIN', '2023-07-07 01:21:15');
INSERT INTO weblog.t_user_role (id, username, role, create_time) VALUES (2, 'test', 'ROLE_VISITOR', '2023-07-07 01:23:33');