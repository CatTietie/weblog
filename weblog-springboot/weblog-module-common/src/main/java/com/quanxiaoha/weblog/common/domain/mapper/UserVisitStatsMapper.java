package com.quanxiaoha.weblog.common.domain.mapper;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.quanxiaoha.weblog.common.domain.dos.UserVisitStatsDO;
import com.quanxiaoha.weblog.common.model.vo.PieDataVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户访问统计 持久层
 *
 * @author lixing
 */
public interface UserVisitStatsMapper extends BaseMapper<UserVisitStatsDO> {

    @Select("SELECT os_name as OsName,count(1) as count FROM user_visit_stats WHERE device_type = #{device} GROUP BY os_name")
    List<JSONObject> countOsByDevice(@Param("device") String device);

    @Select("SELECT device_type as name ,COUNT(1) as value FROM user_visit_stats GROUP BY device_type")
    List<PieDataVO> countByDevice();

    @Select("SELECT COUNT(1) as value FROM user_visit_stats WHERE page_url like '%admin%' ")
    Long countByAdmin();

    @Select("SELECT COUNT(1) as value FROM user_visit_stats WHERE page_url not like '%admin%' ")
    Long countByFront();

    @Select("SELECT TRIM(browser_name) AS xData, COUNT(1) AS seriesData\n" +
            "FROM user_visit_stats\n" +
            "GROUP BY TRIM(browser_name);")
    List<JSONObject> countByBrowser();

    @Select("SELECT count(1) as count\n" +
            "FROM user_visit_stats\n" +
            "WHERE TIME(visit_time) >= #{startTime}\n" +
            "  AND TIME(visit_time) <= #{endTime}\n" +
            "AND visit_time < #{date}\n" +
            "AND device_type = #{device}")
    Long countByPeriod(@Param("date") LocalDateTime date,
                       @Param("startTime") String start,
                       @Param("endTime") String end,
                       @Param("device") String device);

    @Select("SELECT count(1) as count\n" +
            "FROM user_visit_stats\n" +
            "WHERE TIME(visit_time) >= #{startTime}\n" +
            "  AND TIME(visit_time) <= #{endTime}\n" +
            "AND visit_time < #{date}\n" +
            "AND page_url like concat('%',#{pageUrl},'%')")
    Long countByPage(@Param("date") LocalDateTime date,
                     @Param("startTime") String start,
                     @Param("endTime") String end,
                     @Param("pageUrl") String pageUrl);

    @Select("SELECT count(1) as count\n" +
            "FROM user_visit_stats\n" +
            "WHERE TIME(visit_time) >= #{startTime}\n" +
            "  AND TIME(visit_time) <= #{endTime}\n" +
            "AND visit_time < #{date}\n" +
            "AND page_url NOT like concat('%',#{pageUrl},'%')")
    Long countByPageUrl(@Param("date") LocalDateTime date,
                     @Param("startTime") String start,
                     @Param("endTime") String end,
                     @Param("pageUrl") String pageUrl);
}
