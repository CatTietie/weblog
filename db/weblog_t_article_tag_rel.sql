create table t_article_tag_rel
(
    id         bigint unsigned auto_increment comment 'id'
        primary key,
    article_id bigint unsigned not null comment '文章id',
    tag_id     bigint unsigned not null comment '标签id'
)
    comment '文章对应标签关联表' charset = utf8mb4;

create index idx_article_id
    on t_article_tag_rel (article_id);

create index idx_tag_id
    on t_article_tag_rel (tag_id);

INSERT INTO weblog.t_article_tag_rel (id, article_id, tag_id) VALUES (107, 18, 43);
INSERT INTO weblog.t_article_tag_rel (id, article_id, tag_id) VALUES (108, 18, 44);
INSERT INTO weblog.t_article_tag_rel (id, article_id, tag_id) VALUES (109, 19, 40);
INSERT INTO weblog.t_article_tag_rel (id, article_id, tag_id) VALUES (110, 17, 38);