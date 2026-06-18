package com.quanxiaoha.weblog.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.quanxiaoha.weblog.admin.event.ApplicationStatusChangedEvent;
import com.quanxiaoha.weblog.admin.model.vo.resumeapplication.*;
import com.quanxiaoha.weblog.admin.service.AdminResumeApplicationService;
import com.quanxiaoha.weblog.common.constant.Constants;
import com.quanxiaoha.weblog.common.domain.dos.ResumeApplicationDO;
import com.quanxiaoha.weblog.common.domain.dos.ResumeDO;
import com.quanxiaoha.weblog.common.domain.dos.UserDO;
import com.quanxiaoha.weblog.common.domain.mapper.ResumeApplicationMapper;
import com.quanxiaoha.weblog.common.domain.mapper.ResumeMapper;
import com.quanxiaoha.weblog.common.domain.mapper.UserMapper;
import com.quanxiaoha.weblog.common.enums.ResponseCodeEnum;
import com.quanxiaoha.weblog.common.exception.BizException;
import com.quanxiaoha.weblog.common.model.vo.LineDataVO;
import com.quanxiaoha.weblog.common.model.vo.PieDataVO;
import com.quanxiaoha.weblog.common.utils.PageResponse;
import com.quanxiaoha.weblog.common.utils.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AdminResumeApplicationServiceImpl implements AdminResumeApplicationService {

    @Autowired
    private ResumeApplicationMapper resumeApplicationMapper;

    @Autowired
    private ResumeMapper resumeMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new BizException(ResponseCodeEnum.UNAUTHORIZED);
        }
        String username = authentication.getName();
        UserDO userDO = userMapper.findByUsername(username);
        if (userDO == null) {
            throw new BizException(ResponseCodeEnum.UNAUTHORIZED);
        }
        return userDO.getId();
    }

    @Override
    public Response addApplication(AddResumeApplicationReqVO vo) {
        Long userId = getCurrentUserId();

        ResumeDO resume = resumeMapper.selectByIdAndUserId(vo.getResumeId(), userId);
        if (Objects.isNull(resume)) {
            throw new BizException(ResponseCodeEnum.RESUME_NOT_BELONG_TO_USER);
        }

        ResumeApplicationDO applicationDO = ResumeApplicationDO.builder()
                .resumeId(vo.getResumeId())
                .userId(userId)
                .company(vo.getCompany().trim())
                .position(vo.getPosition())
                .applyTime(vo.getApplyTime() != null ? vo.getApplyTime() : LocalDateTime.now())
                .channel(vo.getChannel())
                .status(vo.getStatus() != null ? vo.getStatus() : 0)
                .remark(vo.getRemark())
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();

        resumeApplicationMapper.insert(applicationDO);
        return Response.success();
    }

    @Override
    public PageResponse findApplicationPageList(FindResumeApplicationPageListReqVO vo) {
        Long userId = getCurrentUserId();

        ResumeDO resume = resumeMapper.selectByIdAndUserId(vo.getResumeId(), userId);
        if (Objects.isNull(resume)) {
            throw new BizException(ResponseCodeEnum.RESUME_NOT_BELONG_TO_USER);
        }

        Page<ResumeApplicationDO> page = resumeApplicationMapper.selectPageList(
                vo.getCurrent(), vo.getSize(), vo.getResumeId(), userId, vo.getStatus());

        List<FindResumeApplicationPageListRspVO> vos = page.getRecords().stream()
                .map(d -> FindResumeApplicationPageListRspVO.builder()
                        .id(d.getId())
                        .resumeId(d.getResumeId())
                        .company(d.getCompany())
                        .position(d.getPosition())
                        .applyTime(d.getApplyTime())
                        .channel(d.getChannel())
                        .status(d.getStatus())
                        .remark(d.getRemark())
                        .createTime(d.getCreateTime())
                        .updateTime(d.getUpdateTime())
                        .build())
                .collect(Collectors.toList());

        return PageResponse.success(page, vos);
    }

    @Override
    public Response updateApplication(UpdateResumeApplicationReqVO vo) {
        Long userId = getCurrentUserId();

        ResumeApplicationDO existing = resumeApplicationMapper.selectByIdAndUserId(vo.getId(), userId);
        if (Objects.isNull(existing)) {
            throw new BizException(ResponseCodeEnum.APPLICATION_RECORD_NOT_BELONG_TO_USER);
        }

        Integer oldStatus = existing.getStatus();

        if (Objects.nonNull(vo.getCompany())) existing.setCompany(vo.getCompany().trim());
        if (Objects.nonNull(vo.getPosition())) existing.setPosition(vo.getPosition());
        if (Objects.nonNull(vo.getApplyTime())) existing.setApplyTime(vo.getApplyTime());
        if (Objects.nonNull(vo.getChannel())) existing.setChannel(vo.getChannel());
        if (Objects.nonNull(vo.getStatus())) existing.setStatus(vo.getStatus());
        if (Objects.nonNull(vo.getRemark())) existing.setRemark(vo.getRemark());
        existing.setUpdateTime(LocalDateTime.now());

        resumeApplicationMapper.updateById(existing);

        // 状态真正变更时发送邮件通知
        if (Objects.nonNull(vo.getStatus()) && !vo.getStatus().equals(oldStatus)) {
            UserDO user = userMapper.selectById(userId);
            String email = (user != null) ? user.getEmail() : null;
            eventPublisher.publishEvent(new ApplicationStatusChangedEvent(
                    this, email, existing.getCompany(), existing.getPosition(), oldStatus, vo.getStatus()
            ));
        }

        return Response.success();
    }

    @Override
    public Response deleteApplication(DeleteResumeApplicationReqVO vo) {
        Long userId = getCurrentUserId();

        ResumeApplicationDO existing = resumeApplicationMapper.selectByIdAndUserId(vo.getId(), userId);
        if (Objects.isNull(existing)) {
            throw new BizException(ResponseCodeEnum.APPLICATION_RECORD_NOT_BELONG_TO_USER);
        }

        resumeApplicationMapper.deleteById(vo.getId());
        return Response.success();
    }

    @Override
    public Response getApplicationStatistics() {
        Long userId = getCurrentUserId();

        // 总数
        LambdaQueryWrapper<ResumeApplicationDO> countWrapper = new LambdaQueryWrapper<>();
        countWrapper.eq(ResumeApplicationDO::getUserId, userId);
        Long totalCount = resumeApplicationMapper.selectCount(countWrapper);

        if (totalCount == 0) {
            return Response.success(FindResumeApplicationStatisticsRspVO.builder()
                    .totalCount(0L)
                    .dates(Collections.emptyList())
                    .dailyCounts(Collections.emptyList())
                    .statusDistribution(Collections.emptyList())
                    .build());
        }

        // 近30天每日投递量
        LocalDateTime startTime = LocalDate.now().minusDays(30).atStartOfDay();
        List<LineDataVO> dailyRaw = resumeApplicationMapper.selectDailyCount(userId, startTime);

        Map<LocalDate, Long> dailyMap = dailyRaw.stream()
                .collect(Collectors.toMap(LineDataVO::getXData, LineDataVO::getSeriesData));

        List<String> dates = new ArrayList<>();
        List<Long> dailyCounts = new ArrayList<>();
        LocalDate currDate = LocalDate.now();
        LocalDate tmpDate = currDate.minusDays(30);
        for (; tmpDate.isBefore(currDate) || tmpDate.isEqual(currDate); tmpDate = tmpDate.plusDays(1)) {
            dates.add(tmpDate.format(Constants.MONTH_DAY_FORMATTER));
            Long count = dailyMap.get(tmpDate);
            dailyCounts.add(count == null ? 0L : count);
        }

        // 状态分布
        Map<Integer, String> statusLabelMap = new HashMap<>();
        statusLabelMap.put(0, "已投递");
        statusLabelMap.put(1, "面试中");
        statusLabelMap.put(2, "已录用");
        statusLabelMap.put(3, "已拒绝");
        statusLabelMap.put(4, "已放弃");

        List<Map<String, Object>> statusRaw = resumeApplicationMapper.selectStatusCount(userId);
        List<PieDataVO> statusDistribution = statusRaw.stream()
                .map(m -> {
                    Integer status = ((Number) m.get("name")).intValue();
                    Long value = ((Number) m.get("value")).longValue();
                    String label = statusLabelMap.getOrDefault(status, "未知");
                    return PieDataVO.builder().name(label).value(value).build();
                })
                .collect(Collectors.toList());

        FindResumeApplicationStatisticsRspVO vo = FindResumeApplicationStatisticsRspVO.builder()
                .dates(dates)
                .dailyCounts(dailyCounts)
                .statusDistribution(statusDistribution)
                .totalCount(totalCount)
                .build();

        return Response.success(vo);
    }
}
