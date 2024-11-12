package com.quanxiaoha.weblog.common.domain.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.quanxiaoha.weblog.common.model.vo.PieDataVO;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author lixing
 */
public interface StatisticsMapper extends BaseMapper<PieDataVO> {

    @Select("SELECT tc.name as name, count(article_id) as value\n" +
            "FROM t_category tc\n" +
            "         LEFT JOIN t_article_category_rel tacr on tc.id = tacr.category_id\n" +
            "GROUP BY tc.name")
    List<PieDataVO> categoryAndArticleCount();

    @Select("SELECT tt.name AS name, COUNT(article_id) as value\n" +
            "FROM t_tag tt\n" +
            "         LEFT JOIN t_article_tag_rel tatr on tt.id = tatr.tag_id\n" +
            "WHERE tt.is_deleted = 0\n" +
            "GROUP BY name\n")
    List<PieDataVO> tagsAndArticleCount();
}
