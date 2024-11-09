create table t_article_category_rel
(
    category_id bigint unsigned not null comment '分类id',
    article_id  bigint unsigned not null comment '文章id',
    id          bigint unsigned auto_increment comment 'id'
        primary key,
    constraint uni_article_id
        unique (article_id)
)
    comment '文章所属分类关联表' charset = utf8mb4;

create index idx_category_id
    on t_article_category_rel (category_id);

INSERT INTO weblog.t_article_category_rel (id, article_id, category_id) VALUES (46, 18, 22);
INSERT INTO weblog.t_article_category_rel (id, article_id, category_id) VALUES (47, 19, 24);
INSERT INTO weblog.t_article_category_rel (id, article_id, category_id) VALUES (48, 17, 24);