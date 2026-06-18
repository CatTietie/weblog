package com.quanxiaoha.weblog.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.quanxiaoha.weblog.admin.model.vo.file.UploadFileRspVO;
import com.quanxiaoha.weblog.admin.service.AdminFileService;
import com.quanxiaoha.weblog.admin.utils.AliyunOSSUtility;
import com.quanxiaoha.weblog.common.context.TenantContext;
import com.quanxiaoha.weblog.common.domain.dos.FileDO;
import com.quanxiaoha.weblog.common.domain.mapper.FileMapper;
import com.quanxiaoha.weblog.common.enums.ResponseCodeEnum;
import com.quanxiaoha.weblog.common.exception.BizException;
import com.quanxiaoha.weblog.common.utils.PageResponse;
import com.quanxiaoha.weblog.common.utils.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
@Slf4j
public class AdminFileServiceImpl implements AdminFileService {

    @Autowired
    private AliyunOSSUtility aliyunOSSUtility;

    @Autowired
    private FileMapper fileMapper;

    @Override
    public Response uploadFile(MultipartFile file) {
        try {
            String url = aliyunOSSUtility.uploadFile(file);

            FileDO fileDO = FileDO.builder()
                    .originalName(file.getOriginalFilename())
                    .url(url)
                    .fileSize(file.getSize())
                    .contentType(file.getContentType())
                    .tenantId(TenantContext.getTenantId())
                    .createTime(LocalDateTime.now())
                    .build();
            fileMapper.insert(fileDO);

            return Response.success(UploadFileRspVO.builder().url(url).build());
        } catch (Exception e) {
            log.error("==> 上传文件至阿里云 OSS 错误: ", e);
            throw new BizException(ResponseCodeEnum.FILE_UPLOAD_FAILED);
        }
    }

    @Override
    public PageResponse findFilePageList(Long current, Long size) {
        Page<FileDO> page = new Page<>(current, size);
        LambdaQueryWrapper<FileDO> wrapper = new LambdaQueryWrapper<>();
        Long tenantId = TenantContext.getTenantId();
        if (tenantId != null && tenantId > 0) {
            wrapper.eq(FileDO::getTenantId, tenantId);
        }
        wrapper.orderByDesc(FileDO::getCreateTime);
        Page<FileDO> resultPage = fileMapper.selectPage(page, wrapper);
        return PageResponse.success(resultPage, resultPage.getRecords());
    }

    @Override
    public Response deleteFile(Long id) {
        FileDO fileDO = fileMapper.selectById(id);
        if (Objects.isNull(fileDO)) {
            throw new BizException(ResponseCodeEnum.PARAM_NOT_VALID);
        }
        Long tenantId = TenantContext.getTenantId();
        if (tenantId != null && tenantId > 0 && !Objects.equals(tenantId, fileDO.getTenantId())) {
            throw new BizException(ResponseCodeEnum.TENANT_ACCESS_DENIED);
        }
        fileMapper.deleteById(id);
        return Response.success();
    }
}
