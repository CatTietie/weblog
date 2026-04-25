package com.quanxiaoha.weblog.web;

import com.aliyun.oss.OSS;
import com.quanxiaoha.weblog.admin.config.AliyunOSSProperties;
import com.quanxiaoha.weblog.common.domain.dos.UserDO;
import com.quanxiaoha.weblog.common.domain.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

@SpringBootTest
@Slf4j
class WeblogWebApplicationTests {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private AliyunOSSProperties aliyunOSSProperties;

    @Autowired
    private OSS ossClient;

    @Test
    void testLog() {
        log.info("这是一行 Info 级别日志");
        log.warn("这是一行 Warn 级别日志");
        log.error("这是一行 Error 级别日志");

        String author = "Group 5";
        log.info("这是一行带有占位符日志，作者：{}", author);
    }

    @Test
    void insertTest() {
        UserDO userDO = UserDO.builder()
                .username("Group 5")
                .password("123456")
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .isDeleted(false)
                .build();

        userMapper.insert(userDO);
    }

    @Test
    void uploadFile2OssTest() throws IOException {
        File file = new File("D:\\Backup\\Documents\\My Pictures\\111.jpg");
        String originalFileName = file.getName();

        String key = UUID.randomUUID().toString().replace("-", "");
        String suffix = originalFileName.substring(originalFileName.lastIndexOf("."));
        String objectName = String.format("%s%s", key, suffix);

        log.info("==> 开始上传文件至阿里云 OSS, ObjectName: {}", objectName);
        
        FileInputStream inputStream = new FileInputStream(file);
        try {
            ossClient.putObject(aliyunOSSProperties.getBucketName(), objectName, inputStream);
        } finally {
            inputStream.close();
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
    }

}
