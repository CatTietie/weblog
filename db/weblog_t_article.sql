create table t_article
(
    read_num    int(11) unsigned default 1                 not null comment '被阅读次数',
    is_deleted  tinyint(2)       default 0                 not null comment '删除标志位：0：未删除 1：已删除',
    update_time datetime         default CURRENT_TIMESTAMP not null comment '最后一次更新时间',
    create_time datetime         default CURRENT_TIMESTAMP not null comment '创建时间',
    summary     varchar(160)     default ''                null comment '文章摘要',
    cover       varchar(120)     default ''                not null comment '文章封面',
    title       varchar(120)     default ''                not null comment '文章标题',
    id          bigint unsigned auto_increment comment '文章id'
        primary key
)
    comment '文章表' charset = utf8mb4;

create index idx_create_time
    on t_article (create_time);

INSERT INTO weblog.t_article (id, title, cover, summary, create_time, update_time, is_deleted, read_num) VALUES (17, 'MySQL', 'http://8.140.253.113:9000/weblog/77ca0fd6c8924735b180c942b65346ba.png', 'MySQL知识点', '2024-07-13 17:16:34', '2024-08-04 20:50:05', 0, 48);
INSERT INTO weblog.t_article (id, title, cover, summary, create_time, update_time, is_deleted, read_num) VALUES (18, 'Java基础', 'http://8.140.253.113:9000/weblog/328d2555ada048e4af224ff163a86f88.png', 'JavaSE内容', '2024-07-13 17:21:51', '2024-07-13 17:21:51', 0, 50);
INSERT INTO weblog.t_article (id, title, cover, summary, create_time, update_time, is_deleted, read_num) VALUES (19, 'Redis', 'http://8.140.253.113:9000/weblog/bf4a3dae7fd54c86bb229175bf6f0ee7.png', 'redisson分布式锁', '2024-08-04 15:45:22', '2024-08-04 15:45:22', 0, 46);