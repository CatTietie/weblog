package com.quanxiaoha.weblog.common.domain.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.quanxiaoha.weblog.common.domain.dos.ResumeApplicationDO;
import com.quanxiaoha.weblog.common.model.vo.LineDataVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface ResumeApplicationMapper extends BaseMapper<ResumeApplicationDO> {

    @Select("SELECT DATE(apply_time) AS xData, COUNT(*) AS seriesData FROM t_resume_application WHERE user_id = #{userId} AND apply_time >= #{startTime} GROUP BY DATE(apply_time) ORDER BY xData")
    List<LineDataVO> selectDailyCount(@Param("userId") Long userId, @Param("startTime") LocalDateTime startTime);

    @Select("SELECT status AS name, COUNT(*) AS value FROM t_resume_application WHERE user_id = #{userId} GROUP BY status")
    List<Map<String, Object>> selectStatusCount(@Param("userId") Long userId);

    default Page<ResumeApplicationDO> selectPageList(Long current, Long size, Long resumeId, Long userId, Integer status) {
        Page<ResumeApplicationDO> page = new Page<>(current, size);
        LambdaQueryWrapper<ResumeApplicationDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ResumeApplicationDO::getResumeId, resumeId)
                .eq(ResumeApplicationDO::getUserId, userId)
                .eq(status != null, ResumeApplicationDO::getStatus, status)
                .orderByDesc(ResumeApplicationDO::getApplyTime)
                .orderByDesc(ResumeApplicationDO::getCreateTime);
        return selectPage(page, wrapper);
    }

    default ResumeApplicationDO selectByIdAndUserId(Long id, Long userId) {
        LambdaQueryWrapper<ResumeApplicationDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ResumeApplicationDO::getId, id)
                .eq(ResumeApplicationDO::getUserId, userId);
        return selectOne(wrapper);
    }

    @Select("SELECT * FROM t_resume_application " +
            "WHERE status = 0 " +
            "AND apply_time <= #{cutoffTime} " +
            "AND IFNULL(update_time, apply_time) <= #{cutoffTime}")
    List<ResumeApplicationDO> selectExpiredApplications(@Param("cutoffTime") LocalDateTime cutoffTime);
}
