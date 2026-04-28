package com.quanxiaoha.weblog.admin.utils;

import com.aliyun.oss.OSS;
import com.quanxiaoha.weblog.admin.config.AliyunOSSProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Component
@Slf4j
public class AliyunOSSUtility {

    @Autowired
    private AliyunOSSProperties aliyunOSSProperties;

    @Autowired
    private OSS ossClient;

    public String uploadFile(MultipartFile file) throws Exception {
        if (file == null || file.getSize() == 0) {
            log.error("==> 上传文件异常：文件大小为空 ...");
            throw new RuntimeException("文件大小不能为空");
        }

        String originalFileName = file.getOriginalFilename();
        String contentType = file.getContentType();

        String key = UUID.randomUUID().toString().replace("-", "");
        String suffix = originalFileName.substring(originalFileName.lastIndexOf("."));
        String objectName = String.format("%s%s", key, suffix);

        log.info("==> 开始上传文件至阿里云 OSS, ObjectName: {}", objectName);

        InputStream inputStream = file.getInputStream();
        try {
            ossClient.putObject(aliyunOSSProperties.getBucketName(), objectName, inputStream);
        } catch (Exception e) {
            log.error("==> 上传文件至阿里云 OSS 失败: ", e);
            throw e;
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                log.error("关闭输入流失败", e);
            }
        }

        String url;
        String domain = aliyunOSSProperties.getDomain();
        if (domain != null && !domain.isEmpty()) {
            url = String.format("https://%s/%s", domain, objectName);
        } else {
            url = String.format("https://%s.%s/%s", 
                aliyunOSSProperties.getBucketName(), 
                aliyunOSSProperties.getEndpoint(), 
                objectName);
        }
        
        log.info("==> 上传文件至阿里云 OSS 成功，访问路径: {}", url);
        return url;
    }
}
