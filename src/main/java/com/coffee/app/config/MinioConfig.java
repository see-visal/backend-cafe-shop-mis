package com.coffee.app.config;

import io.minio.MinioClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import lombok.extern.slf4j.Slf4j;

/**
 * MinIO configuration for file storage
 */
@Configuration
@EnableConfigurationProperties(MinioProperties.class)
@Slf4j
public class MinioConfig {

    @Bean
    public MinioClient minioClient(MinioProperties properties) {
        String resolvedEndpoint = resolveEndpoint(properties);
        return MinioClient.builder()
                .endpoint(resolvedEndpoint)
                .credentials(properties.getAccessKey(), properties.getSecretKey())
                .build();
    }

    private String resolveEndpoint(MinioProperties properties) {
        String primary = sanitizeEndpoint(properties.getEndpoint());
        if (primary != null) {
            return primary;
        }

        String fallback = sanitizeEndpoint(properties.getPublicEndpoint());
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

