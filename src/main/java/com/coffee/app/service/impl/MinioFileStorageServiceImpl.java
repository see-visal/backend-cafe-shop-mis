package com.coffee.app.service.impl;

import com.coffee.app.config.MinioProperties;
import com.coffee.app.service.FileStorageService;
import io.minio.BucketExistsArgs;
import io.minio.GetObjectArgs;
import io.minio.GetObjectResponse;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.ListObjectsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.Result;
import io.minio.SetBucketPolicyArgs;
import io.minio.messages.Item;
import jakarta.annotation.PostConstruct;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * MinIO implementation of FileStorageService backed directly by the MinIO SDK.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MinioFileStorageServiceImpl implements FileStorageService {

    private final MinioClient minioClient;
    private final MinioProperties minioProperties;
    private final AtomicBoolean bucketReady = new AtomicBoolean(false);

    @PostConstruct
    void ensureBucketReady() {
        tryEnsureBucketReady("startup");
    }

    @Override
    public String uploadFile(MultipartFile file, String directory) {
        try {
            ensureBucketReadyForWrite();

            if (file.isEmpty()) {
                throw new IllegalArgumentException("File is empty");
            }

            String contentType = file.getContentType();
            if (contentType == null || contentType.isBlank()) {
                contentType = "application/octet-stream";
            }

            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename != null && originalFilename.contains(".")
                    ? originalFilename.substring(originalFilename.lastIndexOf("."))
                    : "";

            String objectName = normalizeDirectory(directory) + UUID.randomUUID() + extension;

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName())
                            .object(objectName)
                            .stream(file.getInputStream(), file.getSize(), -1L)
                            .contentType(contentType)
                            .build()
            );

            String fileUrl = buildPublicFileUrl(objectName);
            log.info("File uploaded successfully: {}", fileUrl);
            return fileUrl;
        } catch (Exception e) {
            log.error("Error uploading file to MinIO: {}", e.getMessage(), e);
            throw new RuntimeException(buildUploadErrorMessage(e), e);
        }
    }

    @Override
    public void deleteFile(String fileUrl) {
        try {
            String objectName = extractObjectName(fileUrl);

            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName())
                            .object(objectName)
                            .build()
            );

            log.info("File deleted successfully: {}", fileUrl);
        } catch (Exception e) {
            log.error("Error deleting file from MinIO: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to delete file: " + e.getMessage(), e);
        }
    }

    @Override
    public String getPresignedUrl(String fileUrl) {
        try {
            String objectName = extractObjectName(fileUrl);
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .bucket(bucketName())
                            .object(objectName)
                            .build()
            );
        } catch (Exception e) {
            log.error("Error generating presigned URL: {}", e.getMessage(), e);
            return fileUrl.startsWith("http://") || fileUrl.startsWith("https://")
                    ? fileUrl
                    : buildPublicFileUrl(extractObjectName(fileUrl));
        }
    }

    @Override
    public List<String> listFiles(String directory) {
        String normalizedDirectory = normalizeDirectory(directory);

        try {
            Iterable<Result<Item>> results = minioClient.listObjects(
                    ListObjectsArgs.builder()
                            .bucket(bucketName())
                            .prefix(normalizedDirectory)
                            .recursive(true)
                            .build()
            );

            List<String> files = new ArrayList<>();
            for (Result<Item> result : results) {
                Item item = result.get();
                if (!item.isDir()) {
                    files.add(item.objectName());
                }
            }
            return files;
        } catch (Exception e) {
            log.warn("Failed to list MinIO files for '{}': {}", normalizedDirectory, e.getMessage());
            return List.of();
        }
    }

    @Override
    public boolean bucketExists() {
        try {
            boolean exists = minioClient.bucketExists(
                    BucketExistsArgs.builder().bucket(bucketName()).build()
            );
            bucketReady.set(exists);
            return exists;
        } catch (Exception e) {
            bucketReady.set(false);
            log.warn("Failed to check MinIO bucket '{}': {}", bucketName(), e.getMessage());
            return false;
        }
    }

    @Override
    public StoredFile downloadFile(String fileUrl) {
        String objectName = extractObjectName(fileUrl);

        try (GetObjectResponse response = minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(bucketName())
                        .object(objectName)
                        .build()
        )) {
            String contentType = response.headers().get("Content-Type");
            if (contentType == null || contentType.isBlank()) {
                contentType = URLConnection.guessContentTypeFromName(objectName);
            }
            if (contentType == null || contentType.isBlank()) {
                contentType = "application/octet-stream";
            }

            return new StoredFile(response.readAllBytes(), contentType);
        } catch (Exception e) {
            throw new RuntimeException("Failed to download file: " + e.getMessage(), e);
        }
    }

    private boolean tryEnsureBucketReady(String context) {
        try {
            boolean exists = minioClient.bucketExists(
                    BucketExistsArgs.builder().bucket(bucketName()).build()
            );

            if (!exists) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName()).build());
                log.info("Created MinIO bucket: {}", bucketName());
            }

            String policy = "{"
                    + "\"Version\":\"2012-10-17\","
                    + "\"Statement\":[{"
                    + "\"Effect\":\"Allow\","
                    + "\"Principal\":{\"AWS\":[\"*\"]},"
                    + "\"Action\":[\"s3:GetObject\"],"
                    + "\"Resource\":[\"arn:aws:s3:::" + bucketName() + "/*\"]"
                    + "}]}";

            minioClient.setBucketPolicy(
                    SetBucketPolicyArgs.builder()
                            .bucket(bucketName())
                            .config(policy)
                            .build()
            );

            bucketReady.set(true);
            return true;
        } catch (Exception e) {
            bucketReady.set(false);
            log.warn("MinIO bucket '{}' is not ready during {}: {}", bucketName(), context, e.getMessage());
            log.warn("Backend will continue starting. File upload features need a working internal MinIO endpoint, an existing bucket, and a public MinIO URL.");
            return false;
        }
    }

    private void ensureBucketReadyForWrite() {
        if (bucketReady.get()) {
            return;
        }

        tryEnsureBucketReady("upload");
    }

    private String buildUploadErrorMessage(Exception e) {
        String raw = e.getMessage() == null ? "unknown storage error" : e.getMessage();

        if (raw.contains("Response code: 404") || raw.contains("Not Found")) {
            return "Failed to upload file: MinIO is reachable but not ready for S3 API requests. In Render, set MINIO_ENDPOINT to the MinIO service internal URL from Connect, keep MINIO_PUBLIC_ENDPOINT as the public browser URL, and create the '" + bucketName() + "' bucket.";
        }

        if (raw.contains("invalid hostname") || raw.contains("<render-minio-internal-host>")) {
            return "Failed to upload file: MINIO_ENDPOINT is still a placeholder. Replace it with the real MinIO internal URL from Render Connect.";
        }

        return "Failed to upload file: " + raw;
    }

    private String extractObjectName(String fileUrl) {
        String value = fileUrl == null ? "" : fileUrl.trim();
        String browserPrefix = "/browser/" + bucketName() + "/";
        String bucketPrefix = bucketName() + "/";
        String pathBucketPrefix = "/" + bucketPrefix;

        int browserBucketIndex = value.indexOf(browserPrefix);
        if (browserBucketIndex >= 0) {
            String encodedObjectName = value.substring(browserBucketIndex + browserPrefix.length());
            String objectName = java.net.URLDecoder.decode(encodedObjectName, java.nio.charset.StandardCharsets.UTF_8)
                    .replaceFirst("^/+", "");
            if (!objectName.isBlank()) {
                return objectName;
            }
        }

        int absoluteBucketIndex = value.indexOf(pathBucketPrefix);
        if (absoluteBucketIndex >= 0) {
            return value.substring(absoluteBucketIndex + pathBucketPrefix.length());
        }
        if (value.startsWith(bucketPrefix)) {
            return value.substring(bucketPrefix.length());
        }
        if (value.startsWith(pathBucketPrefix)) {
            return value.substring(pathBucketPrefix.length());
        }
        if (value.startsWith("/")) {
            return value.substring(1);
        }
        return value;
    }

    private String buildPublicFileUrl(String objectName) {
        return sanitizePublicEndpoint(resolvePublicEndpoint()) + "/" + bucketName() + "/" + objectName;
    }

    private String resolvePublicEndpoint() {
        String publicEndpoint = sanitizeConfiguredEndpoint(minioProperties.getPublicEndpoint());
        if (publicEndpoint != null) {
            return publicEndpoint;
        }
        String endpoint = sanitizeConfiguredEndpoint(minioProperties.getEndpoint());
        if (endpoint != null) {
            return endpoint;
        }
        return "http://localhost:9000";
    }

    private String bucketName() {
        return minioProperties.getBucketName();
    }

    private String normalizeDirectory(String directory) {
        String value = directory == null ? "" : directory.trim().replace('\\', '/').replaceAll("^/+", "");
        if (value.isEmpty()) {
            return "";
        }
        return value.endsWith("/") ? value : value + "/";
    }

    private String sanitizePublicEndpoint(String endpoint) {
        String value = endpoint == null ? "" : endpoint.trim();
        if (value.isEmpty()) {
            return "http://localhost:9000";
        }

        value = value.replaceAll("/+$", "");
        return value.replaceFirst("/browser/?$", "");
    }

    private String sanitizeConfiguredEndpoint(String endpoint) {
        String value = endpoint == null ? "" : endpoint.trim();
        if (value.isEmpty() || value.contains("<") || value.contains(">")) {
            return null;
        }
        if (!(value.startsWith("http://") || value.startsWith("https://"))) {
            return null;
        }
        return value;
    }
}
