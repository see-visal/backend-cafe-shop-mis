package com.coffee.app.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * File storage service interface for handling file uploads
 */
public interface FileStorageService {
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
}

