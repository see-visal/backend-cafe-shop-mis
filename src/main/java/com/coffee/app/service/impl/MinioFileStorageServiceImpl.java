package com.coffee.app.service.impl;

import com.coffee.app.service.FileStorageService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * MinIO implementation of FileStorageService
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MinioFileStorageServiceImpl implements FileStorageService {
    private static final Duration CONSOLE_TIMEOUT = Duration.ofSeconds(30);
    private static final List<String> IMAGE_EXTENSIONS = List.of(".jpg", ".jpeg", ".png", ".webp", ".gif", ".avif", ".svg");

    private final MinioClient minioClient;
    private final AtomicBoolean bucketReady = new AtomicBoolean(false);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${minio.bucket-name}")
    private String bucketName;


    @Value("${minio.public-endpoint:${minio.endpoint}}")
    private String publicEndpoint;

    @Value("${minio.access-key}")
    private String accessKey;

    @Value("${minio.secret-key}")
    private String secretKey;

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

    @Override
    public List<String> listFiles(String directory) {
        String normalizedDirectory = normalizeDirectory(directory);

        try {
            String consoleBase = sanitizePublicEndpoint(publicEndpoint);
            HttpClient client = createAuthenticatedConsoleClient(consoleBase);
            String url = consoleBase
                    + "/api/v1/buckets/"
                    + encodePathSegment(bucketName)
                    + "/objects?prefix="
                    + encodeQueryValue(normalizedDirectory)
                    + "&recursive=true";

            HttpRequest request = HttpRequest.newBuilder(URI.create(url))
                    .timeout(CONSOLE_TIMEOUT)
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new IllegalStateException("MinIO object list failed with status " + response.statusCode());
            }

            JsonNode root = objectMapper.readTree(response.body());
            List<String> files = new ArrayList<>();
            for (JsonNode objectNode : root.path("objects")) {
                String objectName = objectNode.path("name").asText("");
                if (!objectName.isBlank() && isImageFile(objectName)) {
                    files.add(objectName);
                }
            }
            return files;
        } catch (Exception e) {
            log.warn("Failed to list MinIO files for '{}': {}", normalizedDirectory, e.getMessage());
            return List.of();
        }
    }

    @Override
    public StoredFile downloadFile(String fileUrl) {
        String objectName = extractObjectName(fileUrl);

        try {
            String consoleBase = resolveConsoleBase(fileUrl);
            HttpClient client = createAuthenticatedConsoleClient(consoleBase);
            String url = consoleBase
                    + "/api/v1/buckets/"
                    + encodePathSegment(bucketName)
                    + "/objects/download?prefix="
                    + encodeQueryValue(objectName);

            HttpRequest request = HttpRequest.newBuilder(URI.create(url))
                    .timeout(CONSOLE_TIMEOUT)
                    .GET()
                    .build();

            HttpResponse<byte[]> response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());
            String contentType = response.headers().firstValue("Content-Type").orElse("application/octet-stream");
            if (response.statusCode() != 200 || !isRenderableContentType(contentType)) {
                throw new IllegalStateException("MinIO download failed with status " + response.statusCode() + " and content type " + contentType);
            }

            return new StoredFile(response.body(), contentType);
        } catch (Exception consoleException) {
            log.warn("Falling back to direct MinIO file URL for '{}': {}", objectName, consoleException.getMessage());
            return downloadDirectFile(fileUrl, objectName);
        }
    }

    private String buildPublicFileUrl(String objectName) {
        String base = sanitizePublicEndpoint(publicEndpoint);
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
        String browserPrefix = "/browser/" + bucketName + "/";

        String bucketPrefix = bucketName + "/";
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

    private String resolveConsoleBase(String fileUrl) {
        if (fileUrl != null && (fileUrl.startsWith("http://") || fileUrl.startsWith("https://"))) {
            try {
                URI uri = URI.create(fileUrl.trim());
                StringBuilder base = new StringBuilder()
                        .append(uri.getScheme())
                        .append("://")
                        .append(uri.getHost());
                if (uri.getPort() > 0) {
                    base.append(":").append(uri.getPort());
                }
                return sanitizePublicEndpoint(base.toString());
            } catch (Exception ignored) {
                // Fall back to configured public endpoint below.
            }
        }

        return sanitizePublicEndpoint(publicEndpoint);
    }

    private HttpClient createAuthenticatedConsoleClient(String consoleBase) throws Exception {
        CookieManager cookieManager = new CookieManager();
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);

        HttpClient client = HttpClient.newBuilder()
                .cookieHandler(cookieManager)
                .followRedirects(HttpClient.Redirect.NORMAL)
                .connectTimeout(CONSOLE_TIMEOUT)
                .build();

        String loginPayload = objectMapper.writeValueAsString(Map.of(
                "accessKey", accessKey,
                "secretKey", secretKey
        ));

        HttpRequest loginRequest = HttpRequest.newBuilder(URI.create(consoleBase + "/api/v1/login"))
                .timeout(CONSOLE_TIMEOUT)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(loginPayload))
                .build();

        HttpResponse<String> loginResponse = client.send(loginRequest, HttpResponse.BodyHandlers.ofString());
        if (loginResponse.statusCode() != 204) {
            throw new IllegalStateException("MinIO console login failed with status " + loginResponse.statusCode());
        }

        return client;
    }

    private StoredFile downloadDirectFile(String fileUrl, String objectName) {
        try {
            String directUrl;
            if (fileUrl != null && (fileUrl.startsWith("http://") || fileUrl.startsWith("https://")) && !fileUrl.contains("/browser/")) {
                directUrl = fileUrl.trim();
            } else {
                directUrl = buildPublicFileUrl(objectName);
            }

            HttpClient client = HttpClient.newBuilder()
                    .connectTimeout(CONSOLE_TIMEOUT)
                    .followRedirects(HttpClient.Redirect.NORMAL)
                    .build();

            HttpRequest request = HttpRequest.newBuilder(URI.create(directUrl))
                    .timeout(CONSOLE_TIMEOUT)
                    .GET()
                    .build();

            HttpResponse<byte[]> response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());
            String contentType = response.headers().firstValue("Content-Type").orElse("application/octet-stream");
            if (response.statusCode() != 200 || !isRenderableContentType(contentType)) {
                throw new IllegalStateException("Direct image fetch failed with status " + response.statusCode() + " and content type " + contentType);
            }

            return new StoredFile(response.body(), contentType);
        } catch (Exception e) {
            throw new RuntimeException("Failed to download file: " + e.getMessage(), e);
        }
    }

    private boolean isRenderableContentType(String contentType) {
        return contentType != null && contentType.toLowerCase().startsWith("image/");
    }

    private boolean isImageFile(String objectName) {
        String lower = objectName.toLowerCase();
        return IMAGE_EXTENSIONS.stream().anyMatch(lower::endsWith);
    }

    private String normalizeDirectory(String directory) {
        String value = directory == null ? "" : directory.trim().replace('\\', '/').replaceAll("^/+", "");
        if (value.isEmpty()) {
            return "";
        }
        return value.endsWith("/") ? value : value + "/";
    }

    private String encodeQueryValue(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    private String encodePathSegment(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8).replace("+", "%20");
    }

    private String sanitizePublicEndpoint(String endpoint) {
        String value = endpoint == null ? "" : endpoint.trim();
        if (value.isEmpty()) {
            return "http://localhost:9000";
        }

        value = value.replaceAll("/+$", "");
        return value.replaceFirst("/browser/?$", "");
    }
}

