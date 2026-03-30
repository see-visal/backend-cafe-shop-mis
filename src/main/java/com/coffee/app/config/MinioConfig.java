package com.coffee.app.config;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import lombok.extern.slf4j.Slf4j;

/**
 * MinIO configuration for file storage
 */
@Configuration
@Slf4j
public class MinioConfig {

    @Value("${minio.endpoint}")
    private String endpoint;

    @Value("${minio.public-endpoint:${minio.endpoint}}")
    private String publicEndpoint;

    @Value("${minio.access-key}")
    private String accessKey;

    @Value("${minio.secret-key}")
    private String secretKey;

    @Bean
    public MinioClient minioClient() {
        String resolvedEndpoint = resolveEndpoint();
        return MinioClient.builder()
                .endpoint(resolvedEndpoint)
                .credentials(accessKey, secretKey)
                .build();
    }

    private String resolveEndpoint() {
        String primary = sanitizeEndpoint(endpoint);
        if (primary != null) {
            return primary;
        }

        String fallback = sanitizeEndpoint(publicEndpoint);
        if (fallback != null) {
            log.warn("MinIO endpoint was invalid or left as a placeholder. Falling back to public endpoint: {}", fallback);
            return fallback;
        }

        throw new IllegalArgumentException("No valid MinIO endpoint configured");
    }

    private String sanitizeEndpoint(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            return null;
        }
        if (trimmed.contains("<") || trimmed.contains(">")) {
            return null;
        }
        if (!(trimmed.startsWith("http://") || trimmed.startsWith("https://"))) {
            return null;
        }
        return trimmed;
    }
}

