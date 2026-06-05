package com.quanxiaoha.weblog.admin.service.impl;

import com.quanxiaoha.weblog.admin.model.vo.resume.*;
import com.quanxiaoha.weblog.admin.service.AdminResumeService;
import com.quanxiaoha.weblog.common.constant.ResumeConstants;
import com.quanxiaoha.weblog.common.domain.dos.ResumeDO;
import com.quanxiaoha.weblog.common.domain.dos.UserDO;
import com.quanxiaoha.weblog.common.domain.mapper.ResumeMapper;
import com.quanxiaoha.weblog.common.domain.mapper.UserMapper;
import com.quanxiaoha.weblog.common.enums.ResponseCodeEnum;
import com.quanxiaoha.weblog.common.exception.BizException;
import com.quanxiaoha.weblog.common.utils.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AdminResumeServiceImpl implements AdminResumeService {

    @Autowired
    private ResumeMapper resumeMapper;

    @Autowired
    private UserMapper userMapper;

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
    public Response findResumeList() {
        Long userId = getCurrentUserId();
        List<ResumeDO> resumeDOS = resumeMapper.selectByUserId(userId);

        List<ResumeListRspVO> vos = resumeDOS.stream()
                .map(r -> ResumeListRspVO.builder()
                        .id(r.getId())
                        .name(r.getName())
                        .templateId(r.getTemplateId())
                        .shareEnabled(r.getShareEnabled() != null && r.getShareEnabled() == 1)
                        .shareCode(r.getShareCode())
                        .updateTime(r.getUpdateTime())
                        .build())
                .collect(Collectors.toList());

        return Response.success(vos);
    }

    @Override
    public Response findResumeDetail(FindResumeDetailReqVO findResumeDetailReqVO) {
        Long userId = getCurrentUserId();
        ResumeDO resumeDO = resumeMapper.selectByIdAndUserId(findResumeDetailReqVO.getResumeId(), userId);

        if (Objects.isNull(resumeDO)) {
            throw new BizException(ResponseCodeEnum.RESUME_NOT_FOUND);
        }

        ResumeDetailRspVO vo = ResumeDetailRspVO.builder()
                .id(resumeDO.getId())
                .name(resumeDO.getName())
                .content(resumeDO.getContent())
                .templateId(resumeDO.getTemplateId())
                .coverData(resumeDO.getCoverData())
                .languages(resumeDO.getLanguages())
                .createTime(resumeDO.getCreateTime())
                .updateTime(resumeDO.getUpdateTime())
                .build();

        return Response.success(vo);
    }

    @Override
    public Response createResume(CreateResumeReqVO createResumeReqVO) {
        Long userId = getCurrentUserId();

        ResumeDO resumeDO = ResumeDO.builder()
                .userId(userId)
                .name(createResumeReqVO.getName().trim())
                .content(ResumeConstants.DEFAULT_RESUME_CONTENT)
                .templateId(ResumeConstants.DEFAULT_TEMPLATE_ID)
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();

        resumeMapper.insert(resumeDO);

        ResumeDetailRspVO vo = ResumeDetailRspVO.builder()
                .id(resumeDO.getId())
                .name(resumeDO.getName())
                .content(resumeDO.getContent())
                .templateId(resumeDO.getTemplateId())
                .coverData(resumeDO.getCoverData())
                .createTime(resumeDO.getCreateTime())
                .updateTime(resumeDO.getUpdateTime())
                .build();

        return Response.success(vo);
    }

    @Override
    public Response updateResume(UpdateResumeReqVO updateResumeReqVO) {
        Long userId = getCurrentUserId();
        ResumeDO resumeDO = resumeMapper.selectByIdAndUserId(updateResumeReqVO.getId(), userId);

        if (Objects.isNull(resumeDO)) {
            throw new BizException(ResponseCodeEnum.RESUME_NOT_BELONG_TO_USER);
        }

        if (Objects.nonNull(updateResumeReqVO.getName())) {
            resumeDO.setName(updateResumeReqVO.getName());
        }
        if (Objects.nonNull(updateResumeReqVO.getContent())) {
            resumeDO.setContent(updateResumeReqVO.getContent());
        }
        if (Objects.nonNull(updateResumeReqVO.getTemplateId())) {
            resumeDO.setTemplateId(updateResumeReqVO.getTemplateId());
        }
        if (Objects.nonNull(updateResumeReqVO.getCoverData())) {
            resumeDO.setCoverData(updateResumeReqVO.getCoverData());
        }
        if (Objects.nonNull(updateResumeReqVO.getLanguages())) {
            resumeDO.setLanguages(updateResumeReqVO.getLanguages());
        }
        resumeDO.setUpdateTime(LocalDateTime.now());

        resumeMapper.updateById(resumeDO);

        return Response.success();
    }

    @Override
    public Response deleteResume(DeleteResumeReqVO deleteResumeReqVO) {
        Long userId = getCurrentUserId();
        ResumeDO resumeDO = resumeMapper.selectByIdAndUserId(deleteResumeReqVO.getResumeId(), userId);

        if (Objects.isNull(resumeDO)) {
            throw new BizException(ResponseCodeEnum.RESUME_NOT_BELONG_TO_USER);
        }

        resumeMapper.deleteById(resumeDO.getId());

        return Response.success();
    }

    @Override
    public Response uploadResume(MultipartFile file, Long resumeId, String name) {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !originalFilename.toLowerCase().endsWith(".md")) {
            throw new BizException(ResponseCodeEnum.RESUME_UPLOAD_FILE_INVALID);
        }

        String content;
        try {
            content = new String(file.getBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error("读取上传文件失败", e);
            throw new BizException(ResponseCodeEnum.FILE_UPLOAD_FAILED);
        }

        Long userId = getCurrentUserId();

        if (Objects.nonNull(resumeId)) {
            ResumeDO resumeDO = resumeMapper.selectByIdAndUserId(resumeId, userId);
            if (Objects.nonNull(resumeDO)) {
                resumeDO.setContent(content);
                resumeDO.setUpdateTime(LocalDateTime.now());
                resumeMapper.updateById(resumeDO);

                ResumeDetailRspVO vo = ResumeDetailRspVO.builder()
                        .id(resumeDO.getId())
                        .name(resumeDO.getName())
                        .content(resumeDO.getContent())
                        .templateId(resumeDO.getTemplateId())
                        .coverData(resumeDO.getCoverData())
                        .createTime(resumeDO.getCreateTime())
                        .updateTime(resumeDO.getUpdateTime())
                        .build();
                return Response.success(vo);
            }
        }

        String resumeName = (name != null && !name.trim().isEmpty()) ? name.trim() : originalFilename.replace(".md", "");

        ResumeDO resumeDO = ResumeDO.builder()
                .userId(userId)
                .name(resumeName)
                .content(content)
                .templateId(ResumeConstants.DEFAULT_TEMPLATE_ID)
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();

        resumeMapper.insert(resumeDO);

        ResumeDetailRspVO vo = ResumeDetailRspVO.builder()
                .id(resumeDO.getId())
                .name(resumeDO.getName())
                .content(resumeDO.getContent())
                .templateId(resumeDO.getTemplateId())
                .coverData(resumeDO.getCoverData())
                .createTime(resumeDO.getCreateTime())
                .updateTime(resumeDO.getUpdateTime())
                .build();

        return Response.success(vo);
    }

    @Override
    public Response toggleShare(ToggleShareReqVO toggleShareReqVO) {
        Long userId = getCurrentUserId();
        ResumeDO resumeDO = resumeMapper.selectByIdAndUserId(toggleShareReqVO.getResumeId(), userId);

        if (Objects.isNull(resumeDO)) {
            throw new BizException(ResponseCodeEnum.RESUME_NOT_BELONG_TO_USER);
        }

        if (Boolean.TRUE.equals(toggleShareReqVO.getEnabled())) {
            if (resumeDO.getShareCode() == null || resumeDO.getShareCode().isEmpty()) {
                resumeDO.setShareCode(generateShareCode());
            }
            resumeDO.setShareEnabled(1);
        } else {
            resumeDO.setShareEnabled(0);
        }
        resumeDO.setUpdateTime(LocalDateTime.now());
        resumeMapper.updateById(resumeDO);

        ShareInfoRspVO vo = ShareInfoRspVO.builder()
                .shareEnabled(resumeDO.getShareEnabled() == 1)
                .shareCode(resumeDO.getShareCode())
                .build();

        return Response.success(vo);
    }

    @Override
    public Response getShareInfo(FindResumeDetailReqVO findResumeDetailReqVO) {
        Long userId = getCurrentUserId();
        ResumeDO resumeDO = resumeMapper.selectByIdAndUserId(findResumeDetailReqVO.getResumeId(), userId);

        if (Objects.isNull(resumeDO)) {
            throw new BizException(ResponseCodeEnum.RESUME_NOT_FOUND);
        }

        ShareInfoRspVO vo = ShareInfoRspVO.builder()
                .shareEnabled(resumeDO.getShareEnabled() != null && resumeDO.getShareEnabled() == 1)
                .shareCode(resumeDO.getShareCode())
                .build();

        return Response.success(vo);
    }

    private String generateShareCode() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom random = new SecureRandom();
        for (int attempt = 0; attempt < 3; attempt++) {
            StringBuilder sb = new StringBuilder(10);
            for (int i = 0; i < 10; i++) {
                sb.append(chars.charAt(random.nextInt(chars.length())));
            }
            String code = sb.toString();
            if (resumeMapper.selectByShareCode(code) == null) {
                return code;
            }
        }
        StringBuilder sb = new StringBuilder(12);
        for (int i = 0; i < 12; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }
}
