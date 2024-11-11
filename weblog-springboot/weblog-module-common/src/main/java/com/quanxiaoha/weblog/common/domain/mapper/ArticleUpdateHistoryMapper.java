package com.quanxiaoha.weblog.common.domain.mapper;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.quanxiaoha.weblog.common.domain.dos.ArticleUpdateHistoryDO;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author lixing
 */
public interface ArticleUpdateHistoryMapper extends BaseMapper<ArticleUpdateHistoryDO> {

    @Select("SELECT t.title as title,count(1) as count FROM t_article_update_history th LEFT JOIN t_article t on th.article_id = t.id" +
            " WHERE t.is_deleted = 0 GROUP BY t.title ORDER BY count(1) LIMIT 6")
    List<JSONObject> countUpdateTimes();
}
