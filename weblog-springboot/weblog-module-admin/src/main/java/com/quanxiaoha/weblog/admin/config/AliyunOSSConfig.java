package com.quanxiaoha.weblog.admin.config;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@Slf4j
public class AliyunOSSConfig {

    @Autowired
    private AliyunOSSProperties aliyunOSSProperties;

    private static final String PLACEHOLDER_PREFIX = "your-";
    private static final String ENV_KEY_ID = "OSS_ACCESS_KEY_ID";
    private static final String ENV_KEY_SECRET = "OSS_ACCESS_KEY_SECRET";

    @Bean
    public OSS ossClient() {
        log.info("========== OSS Configuration Diagnostic ==========");

        Map<String, String> env = System.getenv();
        log.info("All OSS-related environment variables found:");
        for (Map.Entry<String, String> entry : env.entrySet()) {
            String key = entry.getKey();
            if (key.toLowerCase().contains("oss") || 
                key.toLowerCase().contains("aliyun") || 
                key.toLowerCase().contains("access")) {
                String maskedValue = entry.getValue() == null ? "null" : 
                    "******" + entry.getValue().substring(Math.max(0, entry.getValue().length() - 4));
                log.info("  {} = {}", key, maskedValue);
            }
        }

        String envKeyId = System.getenv(ENV_KEY_ID);
        String envKeySecret = System.getenv(ENV_KEY_SECRET);

        log.info("Direct System.getenv() results:");
        log.info("  {} = {}", ENV_KEY_ID, envKeyId != null ? (envKeyId.isEmpty() ? "(empty string)" : "******" + envKeyId.substring(Math.max(0, envKeyId.length() - 4))) : "null");
        log.info("  {} = {}", ENV_KEY_SECRET, envKeySecret != null ? (envKeySecret.isEmpty() ? "(empty string)" : "******" + envKeySecret.substring(Math.max(0, envKeySecret.length() - 4))) : "null");

        log.info("Config file values (aliyun.oss.*):");
        log.info("  accessKeyId = {}", aliyunOSSProperties.getAccessKeyId() != null ? 
            (aliyunOSSProperties.getAccessKeyId().startsWith(PLACEHOLDER_PREFIX) ? 
                aliyunOSSProperties.getAccessKeyId() : "******" + aliyunOSSProperties.getAccessKeyId().substring(Math.max(0, aliyunOSSProperties.getAccessKeyId().length() - 4))) : "null");
        log.info("  accessKeySecret = {}", aliyunOSSProperties.getAccessKeySecret() != null ? 
            (aliyunOSSProperties.getAccessKeySecret().startsWith(PLACEHOLDER_PREFIX) ? 
                aliyunOSSProperties.getAccessKeySecret() : "******" + aliyunOSSProperties.getAccessKeySecret().substring(Math.max(0, aliyunOSSProperties.getAccessKeySecret().length() - 4))) : "null");

        String accessKeyId = null;
        String accessKeySecret = null;
        String source = null;

        if (isValidKeyValue(envKeyId) && isValidKeyValue(envKeySecret)) {
            accessKeyId = envKeyId;
            accessKeySecret = envKeySecret;
            source = "environment variable (" + ENV_KEY_ID + ")";
            log.info("Using credentials from: {}", source);
        } else {
            log.warn("Environment variables not found or invalid, checking config file...");
            if (isValidKeyValue(aliyunOSSProperties.getAccessKeyId()) && 
                isValidKeyValue(aliyunOSSProperties.getAccessKeySecret())) {
                accessKeyId = aliyunOSSProperties.getAccessKeyId();
                accessKeySecret = aliyunOSSProperties.getAccessKeySecret();
                source = "application config (aliyun.oss.*)";
                log.info("Using credentials from: {}", source);
            }
        }

        if (accessKeyId == null || accessKeySecret == null) {
            log.error("========== OSS Configuration FAILED ==========");
            log.error("Neither environment variables nor config file have valid credentials.");
            log.error("Please verify:");
            log.error("  1. Environment variable: {} is set", ENV_KEY_ID);
            log.error("  2. Environment variable: {} is set", ENV_KEY_SECRET);
            log.error("  3. Values do not start with '{}'", PLACEHOLDER_PREFIX);
            log.error("  4. If using batch file, use 'setx' to set system variables, or restart cmd");
            log.error("================================================");
            throw new IllegalStateException(
                "OSS credentials not found. " +
                "Environment variables checked: " + ENV_KEY_ID + ", " + ENV_KEY_SECRET + ". " +
                "Values are either null, empty, or contain placeholder '" + PLACEHOLDER_PREFIX + "'."
            );
        }

        log.info("========== OSS Configuration SUCCESS ==========");
        log.info("AccessKeyId (last 4 chars): ****{}", accessKeyId.substring(Math.max(0, accessKeyId.length() - 4)));
        log.info("Endpoint: {}", aliyunOSSProperties.getEndpoint());
        log.info("================================================");

        return new OSSClientBuilder().build(
                aliyunOSSProperties.getEndpoint(),
                accessKeyId,
                accessKeySecret
        );
    }

    private boolean isValidKeyValue(String value) {
        if (value == null) {
            log.debug("Value is null");
            return false;
        }
        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            log.debug("Value is empty string");
            return false;
        }
        if (trimmed.startsWith(PLACEHOLDER_PREFIX)) {
            log.debug("Value is placeholder: {}", trimmed);
            return false;
        }
        return true;
    }
}
