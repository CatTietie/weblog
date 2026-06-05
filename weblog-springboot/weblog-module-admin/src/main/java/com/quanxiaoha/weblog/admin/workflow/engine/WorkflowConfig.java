package com.quanxiaoha.weblog.admin.workflow.engine;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class WorkflowConfig {

    @Value("${weblog.site-url:http://localhost:8080}")
    private String siteUrl;

    @Bean
    public RestTemplate workflowRestTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(5000);
        factory.setReadTimeout(10000);
        return new RestTemplate(factory);
    }

    public String getSiteUrl() {
        return siteUrl;
    }
}
