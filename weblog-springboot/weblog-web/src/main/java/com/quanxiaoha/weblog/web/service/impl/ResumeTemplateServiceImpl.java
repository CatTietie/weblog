package com.quanxiaoha.weblog.web.service.impl;

import com.quanxiaoha.weblog.common.domain.dos.ResumeTemplateDO;
import com.quanxiaoha.weblog.common.domain.mapper.ResumeTemplateMapper;
import com.quanxiaoha.weblog.common.utils.Response;
import com.quanxiaoha.weblog.web.model.vo.resumetemplate.FindEnabledResumeTemplateRspVO;
import com.quanxiaoha.weblog.web.service.ResumeTemplateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ResumeTemplateServiceImpl implements ResumeTemplateService {

    @Autowired
    private ResumeTemplateMapper resumeTemplateMapper;

    @Override
    public Response findEnabledTemplateList() {
        List<ResumeTemplateDO> templates = resumeTemplateMapper.selectEnabledList();

        List<FindEnabledResumeTemplateRspVO> vos = templates.stream()
                .map(t -> FindEnabledResumeTemplateRspVO.builder()
                        .id(t.getId())
                        .name(t.getName())
                        .componentName(t.getComponentName())
                        .thumbnail(t.getThumbnail())
                        .description(t.getDescription())
                        .build())
                .collect(Collectors.toList());

        return Response.success(vos);
    }
}
