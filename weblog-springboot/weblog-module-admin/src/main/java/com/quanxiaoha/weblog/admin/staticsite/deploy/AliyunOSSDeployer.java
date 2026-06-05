package com.quanxiaoha.weblog.admin.staticsite.deploy;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.quanxiaoha.weblog.common.domain.dos.StaticSiteConfigDO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class AliyunOSSDeployer implements DeployStrategy {

    private static final Map<String, String> CONTENT_TYPE_MAP = new HashMap<>();

    static {
        CONTENT_TYPE_MAP.put(".html", "text/html");
        CONTENT_TYPE_MAP.put(".css", "text/css");
        CONTENT_TYPE_MAP.put(".js", "application/javascript");
        CONTENT_TYPE_MAP.put(".json", "application/json");
        CONTENT_TYPE_MAP.put(".png", "image/png");
        CONTENT_TYPE_MAP.put(".jpg", "image/jpeg");
        CONTENT_TYPE_MAP.put(".jpeg", "image/jpeg");
        CONTENT_TYPE_MAP.put(".gif", "image/gif");
        CONTENT_TYPE_MAP.put(".svg", "image/svg+xml");
        CONTENT_TYPE_MAP.put(".ico", "image/x-icon");
        CONTENT_TYPE_MAP.put(".xml", "application/xml");
    }

    @Override
    public void deploy(String outputDir, StaticSiteConfigDO config) throws Exception {
        if (!Boolean.TRUE.equals(config.getOssEnabled())) {
            return;
        }

        String endpoint = config.getOssEndpoint();
        String accessKeyId = config.getOssAccessKeyId();
        String accessKeySecret = config.getOssAccessKeySecret();
        String bucketName = config.getOssBucketName();
        String basePath = config.getOssBasePath() != null ? config.getOssBasePath() : "";

        if (!basePath.isEmpty() && !basePath.endsWith("/")) {
            basePath += "/";
        }

        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        try {
            Path source = Paths.get(outputDir);
            final String finalBasePath = basePath;

            Files.walkFileTree(source, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    String relativePath = source.relativize(file).toString().replace("\\", "/");
                    String objectKey = finalBasePath + relativePath;

                    com.aliyun.oss.model.ObjectMetadata metadata = new com.aliyun.oss.model.ObjectMetadata();
                    String contentType = getContentType(file.getFileName().toString());
                    if (contentType != null) {
                        metadata.setContentType(contentType);
                    }

                    ossClient.putObject(bucketName, objectKey, file.toFile(), metadata);
                    return FileVisitResult.CONTINUE;
                }
            });

            log.info("OSS 部署完成: bucket={}, basePath={}", bucketName, finalBasePath);

        } finally {
            ossClient.shutdown();
        }
    }

    private String getContentType(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex > 0) {
            String ext = fileName.substring(dotIndex).toLowerCase();
            return CONTENT_TYPE_MAP.get(ext);
        }
        return null;
    }
}
