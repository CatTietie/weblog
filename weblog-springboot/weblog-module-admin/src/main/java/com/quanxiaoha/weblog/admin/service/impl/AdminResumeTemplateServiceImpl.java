package com.quanxiaoha.weblog.admin.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.quanxiaoha.weblog.admin.model.vo.resumetemplate.*;
import com.quanxiaoha.weblog.admin.service.AdminResumeTemplateService;
import com.quanxiaoha.weblog.common.domain.dos.ResumeTemplateDO;
import com.quanxiaoha.weblog.common.domain.mapper.ResumeTemplateMapper;
import com.quanxiaoha.weblog.common.enums.ResponseCodeEnum;
import com.quanxiaoha.weblog.common.exception.BizException;
import com.quanxiaoha.weblog.common.utils.PageResponse;
import com.quanxiaoha.weblog.common.utils.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AdminResumeTemplateServiceImpl implements AdminResumeTemplateService {

    @Autowired
    private ResumeTemplateMapper resumeTemplateMapper;

    @Override
    public Response addResumeTemplate(AddResumeTemplateReqVO vo) {
        ResumeTemplateDO existByName = resumeTemplateMapper.selectByName(vo.getName());
        if (Objects.nonNull(existByName)) {
            throw new BizException(ResponseCodeEnum.RESUME_TEMPLATE_NAME_IS_EXISTED);
        }

        ResumeTemplateDO existByComponent = resumeTemplateMapper.selectByComponentName(vo.getComponentName());
        if (Objects.nonNull(existByComponent)) {
            throw new BizException(ResponseCodeEnum.RESUME_TEMPLATE_NAME_IS_EXISTED);
        }

        ResumeTemplateDO resumeTemplateDO = ResumeTemplateDO.builder()
                .name(vo.getName())
                .componentName(vo.getComponentName())
                .thumbnail(vo.getThumbnail())
                .description(vo.getDescription())
                .status(1)
                .sortOrder(vo.getSortOrder() != null ? vo.getSortOrder() : 0)
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();

        resumeTemplateMapper.insert(resumeTemplateDO);
        return Response.success();
    }

    @Override
    public PageResponse findResumeTemplatePageList(FindResumeTemplatePageListReqVO vo) {
        Page<ResumeTemplateDO> page = resumeTemplateMapper.selectPageList(vo.getCurrent(), vo.getSize(), vo.getName());

        List<FindResumeTemplatePageListRspVO> vos = page.getRecords().stream()
                .map(d -> FindResumeTemplatePageListRspVO.builder()
                        .id(d.getId())
                        .name(d.getName())
                        .componentName(d.getComponentName())
                        .thumbnail(d.getThumbnail())
                        .description(d.getDescription())
                        .status(d.getStatus())
                        .sortOrder(d.getSortOrder())
                        .createTime(d.getCreateTime())
                        .build())
                .collect(Collectors.toList());

        return PageResponse.success(page, vos);
    }

    @Override
    public Response updateResumeTemplate(UpdateResumeTemplateReqVO vo) {
        ResumeTemplateDO existing = resumeTemplateMapper.selectById(vo.getId());
        if (Objects.isNull(existing)) {
            throw new BizException(ResponseCodeEnum.RESUME_TEMPLATE_NOT_FOUND);
        }

        ResumeTemplateDO existByName = resumeTemplateMapper.selectByName(vo.getName());
        if (Objects.nonNull(existByName) && !existByName.getId().equals(vo.getId())) {
            throw new BizException(ResponseCodeEnum.RESUME_TEMPLATE_NAME_IS_EXISTED);
        }

        existing.setName(vo.getName());
        existing.setComponentName(vo.getComponentName());
        existing.setThumbnail(vo.getThumbnail());
        existing.setDescription(vo.getDescription());
        if (vo.getSortOrder() != null) {
            existing.setSortOrder(vo.getSortOrder());
        }
        existing.setUpdateTime(LocalDateTime.now());

        resumeTemplateMapper.updateById(existing);
        return Response.success();
    }

    @Override
    public Response deleteResumeTemplate(DeleteResumeTemplateReqVO vo) {
        ResumeTemplateDO existing = resumeTemplateMapper.selectById(vo.getId());
        if (Objects.isNull(existing)) {
            throw new BizException(ResponseCodeEnum.RESUME_TEMPLATE_NOT_FOUND);
        }

        resumeTemplateMapper.deleteById(vo.getId());
        return Response.success();
    }

    @Override
    public Response updateResumeTemplateStatus(UpdateResumeTemplateStatusReqVO vo) {
        ResumeTemplateDO existing = resumeTemplateMapper.selectById(vo.getId());
        if (Objects.isNull(existing)) {
            throw new BizException(ResponseCodeEnum.RESUME_TEMPLATE_NOT_FOUND);
        }

        existing.setStatus(vo.getStatus());
        existing.setUpdateTime(LocalDateTime.now());
        resumeTemplateMapper.updateById(existing);
        return Response.success();
    }
}
