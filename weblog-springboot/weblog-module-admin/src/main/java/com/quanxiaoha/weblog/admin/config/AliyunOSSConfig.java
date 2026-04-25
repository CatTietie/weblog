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
        return new OSSClientBuilder().build(
                aliyunOSSProperties.getEndpoint(),
                aliyunOSSProperties.getAccessKeyId(),
                aliyunOSSProperties.getAccessKeySecret()
        );
    }
}
