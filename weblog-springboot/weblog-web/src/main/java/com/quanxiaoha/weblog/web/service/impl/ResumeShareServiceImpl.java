package com.quanxiaoha.weblog.web.service.impl;

import com.quanxiaoha.weblog.common.domain.dos.ResumeDO;
import com.quanxiaoha.weblog.common.domain.mapper.ResumeMapper;
import com.quanxiaoha.weblog.common.enums.ResponseCodeEnum;
import com.quanxiaoha.weblog.common.exception.BizException;
import com.quanxiaoha.weblog.common.utils.Response;
import com.quanxiaoha.weblog.web.model.vo.resume.FindShareResumeRspVO;
import com.quanxiaoha.weblog.web.service.ResumeShareService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ResumeShareServiceImpl implements ResumeShareService {

    @Autowired
    private ResumeMapper resumeMapper;

    @Override
    public Response findSharedResume(String shareCode) {
        ResumeDO resumeDO = resumeMapper.selectByShareCode(shareCode);

        if (resumeDO == null || resumeDO.getShareEnabled() == null || resumeDO.getShareEnabled() != 1) {
            throw new BizException(ResponseCodeEnum.RESUME_SHARE_DISABLED);
        }

        FindShareResumeRspVO vo = FindShareResumeRspVO.builder()
                .name(resumeDO.getName())
                .content(resumeDO.getContent())
                .templateId(resumeDO.getTemplateId())
                .coverData(resumeDO.getCoverData())
                .languages(resumeDO.getLanguages())
                .build();

        return Response.success(vo);
    }
}
