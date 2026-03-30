package com.coffee.app.service.impl;

import com.coffee.app.service.FileStorageService;
import io.minio.BucketExistsArgs;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.SetBucketPolicyArgs;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * MinIO implementation of FileStorageService
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MinioFileStorageServiceImpl implements FileStorageService {

    private final MinioClient minioClient;
    private final AtomicBoolean bucketReady = new AtomicBoolean(false);

    @Value("${minio.bucket-name}")
    private String bucketName;


    @Value("${minio.public-endpoint:${minio.endpoint}}")
    private String publicEndpoint;

    @PostConstruct
    void ensureBucketReady() {
        tryEnsureBucketReady("startup");
    }

    private boolean tryEnsureBucketReady(String context) {
        try {
            boolean exists = minioClient.bucketExists(
                    BucketExistsArgs.builder().bucket(bucketName).build()
            );

            if (!exists) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
                log.info("Created MinIO bucket: {}", bucketName);
            }

            String policy = "{" +
                    "\"Version\":\"2012-10-17\"," +
                    "\"Statement\":[{" +
                    "\"Effect\":\"Allow\"," +
                    "\"Principal\":{\"AWS\":[\"*\"]}," +
                    "\"Action\":[\"s3:GetObject\"]," +
                    "\"Resource\":[\"arn:aws:s3:::" + bucketName + "/*\"]" +
                    "}]}";

            minioClient.setBucketPolicy(
                    SetBucketPolicyArgs.builder()
                            .bucket(bucketName)
                            .config(policy)
                            .build()
            );
            bucketReady.set(true);
            return true;
        } catch (Exception e) {
            bucketReady.set(false);
            log.warn("MinIO bucket '{}' is not ready during {}: {}", bucketName, context, e.getMessage());
            log.warn("Backend will continue starting. File upload features need a working internal MinIO endpoint, an existing bucket, and a public MinIO URL.");
            return false;
        }
    }

    @Override
    public String uploadFile(MultipartFile file, String directory) {
        try {
            ensureBucketReadyForWrite();

            if (file.isEmpty()) {
                throw new IllegalArgumentException("File is empty");
            }

            String contentType = file.getContentType();
            if (contentType == null || contentType.isEmpty()) {
                contentType = "application/octet-stream";
            }

            // Generate unique filename
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename != null && originalFilename.contains(".")
                    ? originalFilename.substring(originalFilename.lastIndexOf("."))
                    : "";
            String filename = UUID.randomUUID() + extension;

            // Construct object name with directory
            String objectName = directory + "/" + filename;

            // Upload to MinIO
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            // Let MinIO SDK choose a valid multipart size for the object length.
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
                            .bucket(bucketName)
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
                            .bucket(bucketName)
                            .object(objectName)
                            .build()
            );

        } catch (Exception e) {
            log.error("Error generating presigned URL: {}", e.getMessage(), e);
            // Return direct URL as fallback
            return fileUrl.startsWith("http://") || fileUrl.startsWith("https://")
                    ? fileUrl
                    : buildPublicFileUrl(extractObjectName(fileUrl));
        }
    }

    private String buildPublicFileUrl(String objectName) {
        String base = publicEndpoint.endsWith("/")
                ? publicEndpoint.substring(0, publicEndpoint.length() - 1)
                : publicEndpoint;
        return base + "/" + bucketName + "/" + objectName;
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
            return "Failed to upload file: MinIO is reachable but not ready for S3 API requests. In Render, set MINIO_ENDPOINT to the MinIO service internal URL from Connect, keep MINIO_PUBLIC_ENDPOINT as the public browser URL, and create the '" + bucketName + "' bucket.";
        }

        if (raw.contains("invalid hostname") || raw.contains("<render-minio-internal-host>")) {
            return "Failed to upload file: MINIO_ENDPOINT is still a placeholder. Replace it with the real MinIO internal URL from Render Connect.";
        }

        return "Failed to upload file: " + raw;
    }

    private String extractObjectName(String fileUrl) {
        String value = fileUrl == null ? "" : fileUrl.trim();

        String bucketPrefix = bucketName + "/";
        String pathBucketPrefix = "/" + bucketPrefix;

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
}

