package com.quanxiaoha.weblog.admin.config;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AliyunOSSConfig {

    @Autowired
    private AliyunOSSProperties aliyunOSSProperties;

    @Bean
    public OSS ossClient() {
        // 从环境变量中读取阿里云 OSS 密钥
        String accessKeyId = System.getenv("OSS_ACCESS_KEY_ID");
        String accessKeySecret = System.getenv("OSS_ACCESS_KEY_SECRET");
        
        // 如果环境变量为空,使用配置文件中的值作为备选
        if (accessKeyId == null || accessKeyId.isEmpty()) {
            accessKeyId = aliyunOSSProperties.getAccessKeyId();
        }
        if (accessKeySecret == null || accessKeySecret.isEmpty()) {
            accessKeySecret = aliyunOSSProperties.getAccessKeySecret();
        }
        
        return new OSSClientBuilder().build(
                aliyunOSSProperties.getEndpoint(),
                accessKeyId,
                accessKeySecret
        );
    }
}
