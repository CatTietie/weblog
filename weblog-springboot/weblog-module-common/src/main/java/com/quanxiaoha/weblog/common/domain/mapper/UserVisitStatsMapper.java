package com.quanxiaoha.weblog.common.domain.mapper;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.quanxiaoha.weblog.common.domain.dos.UserVisitStatsDO;
import com.quanxiaoha.weblog.common.model.vo.PieDataVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

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
}
