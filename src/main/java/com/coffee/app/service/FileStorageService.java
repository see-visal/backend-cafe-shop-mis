package com.coffee.app.service;

import java.util.List;
import org.springframework.web.multipart.MultipartFile;

/**
 * File storage service interface for handling file uploads
 */
public interface FileStorageService {
    record StoredFile(byte[] content, String contentType) {}

    /**
     * Upload a file to storage
     * 
     * @param file the file to upload
     * @param directory the directory/path to store the file in
     * @return the URL or path of the uploaded file
     */
    String uploadFile(MultipartFile file, String directory);

    /**
     * Delete a file from storage
     * 
     * @param fileUrl the URL/path of the file to delete
     */
    void deleteFile(String fileUrl);

    /**
     * Get the presigned URL for a file
     * 
     * @param fileUrl the file URL/path
     * @return the presigned URL
     */
    String getPresignedUrl(String fileUrl);

    /**
     * List files in a storage directory.
     *
     * @param directory directory/path prefix
     * @return object paths inside the bucket
     */
    List<String> listFiles(String directory);

    /**
     * Download file bytes and metadata from storage.
     *
     * @param fileUrl file URL/path/object name
     * @return stored file payload
     */
    StoredFile downloadFile(String fileUrl);
}

