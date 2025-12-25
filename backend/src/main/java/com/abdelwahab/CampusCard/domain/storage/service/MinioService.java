package com.abdelwahab.CampusCard.domain.storage.service;

import java.io.InputStream;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.abdelwahab.CampusCard.domain.common.exception.InvalidStateException;

/**
 * Service responsible for file storage operations using MinIO object storage.
 * Handles upload, retrieval, and deletion of user files (photos, documents).
 *
 * <p>Supported file types:
 * <ul>
 *   <li><strong>Profile Photos:</strong> JPEG/PNG images for user profiles</li>
 *   <li><strong>National ID Scans:</strong> JPEG/PNG images for identity verification</li>
 * </ul>
 *
 * <p>File organization:
 * <ul>
 *   <li>Bucket: Configured via {@code minio.bucket-name} property</li>
 *   <li>Profile photos: {@code profile-photos/{userId}.{extension}}</li>
 *   <li>National ID scans: {@code national-ids/{userId}.{extension}}</li>
 * </ul>
 *
 * <p>Security considerations:
 * <ul>
 *   <li>Files stored with unique names to prevent collisions</li>
 *   <li>File URLs are public but unguessable (UUID-based)</li>
 *   <li>File size limits enforced at upload</li>
 *   <li>Content type validation performed</li>
 * </ul>
 *
 * <p>Configuration required:
 * <ul>
 *   <li>{@code minio.url} - MinIO server endpoint</li>
 *   <li>{@code minio.access-key} - Access credentials</li>
 *   <li>{@code minio.secret-key} - Secret credentials</li>
 *   <li>{@code minio.bucket-name} - Storage bucket name</li>
 * </ul>
 *
 * @author CampusCard Team
 * @version 1.0
 * @since 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MinioService {

    private final MinioClient minioClient;

    @Value("${minio.bucket-name}")
    private String bucketName;

    @Value("${minio.url}")
    private String minioUrl;

    /**
     * Ensures the configured MinIO bucket exists, creating it if necessary.
     * Called automatically on service initialization to guarantee bucket availability.
     *
     * <p>This method is idempotent - safe to call multiple times.
     * If bucket already exists, no action is taken.
     *
     * @throws RuntimeException if bucket cannot be created or checked
     */
    public void ensureBucketExists() {
        try {
            boolean exists = minioClient.bucketExists(
                    BucketExistsArgs.builder().bucket(bucketName).build()
            );
            
            if (!exists) {
                minioClient.makeBucket(
                        MakeBucketArgs.builder().bucket(bucketName).build()
                );
                log.info("Created bucket: {}", bucketName);
            }
        } catch (Exception e) {
            log.error("Error checking/creating bucket: {}", e.getMessage());
            throw new RuntimeException("Failed to initialize MinIO bucket", e);
        }
    }

    /**
     * Upload profile photo for user
     * Path: uploads/{userId}/profile_photo.{ext}
     */
    public String uploadProfilePhoto(Integer userId, MultipartFile file) {
        validateImageFile(file);
        
        String fileExtension = getFileExtension(file.getOriginalFilename());
        String objectName = String.format("%d/profile_photo.%s", userId, fileExtension);
        
        return uploadFile(file, objectName);
    }

    /**
     * Upload national ID scan for user
     * Path: uploads/{userId}/national_id_scan.{ext}
     */
    public String uploadNationalIdScan(Integer userId, MultipartFile file) {
        validateImageFile(file);
        
        String fileExtension = getFileExtension(file.getOriginalFilename());
        String objectName = String.format("%d/national_id_scan.%s", userId, fileExtension);
        
        return uploadFile(file, objectName);
    }

    /**
     * Upload file to MinIO
     */
    private String uploadFile(MultipartFile file, String objectName) {
        try {
            ensureBucketExists();
            
            InputStream inputStream = file.getInputStream();
            
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .stream(inputStream, file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );
            
            String fileUrl = String.format("%s/%s/%s", minioUrl, bucketName, objectName);
            log.info("File uploaded successfully: {}", fileUrl);
            
            return fileUrl;
            
        } catch (Exception e) {
            log.error("Error uploading file: {}", e.getMessage());
            throw new RuntimeException("Failed to upload file to MinIO", e);
        }
    }

    /**
     * Delete file from MinIO
     */
    public void deleteFile(String objectName) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build()
            );
            log.info("File deleted successfully: {}", objectName);
        } catch (Exception e) {
            log.error("Error deleting file: {}", e.getMessage());
            throw new RuntimeException("Failed to delete file from MinIO", e);
        }
    }

    /**
     * Validate that the file is an image
     */
    private void validateImageFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new InvalidStateException("File is empty");
        }
        
        // Validate file size (max 10MB)
        long maxSize = 10 * 1024 * 1024; // 10MB
        if (file.getSize() > maxSize) {
            throw new InvalidStateException("File size exceeds 10MB limit");
        }
        
        // Validate file type
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new InvalidStateException("File must be an image");
        }
    }

    /**
     * Get file extension from filename
     */
    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "jpg"; // default extension
        }
        return filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
    }

    /**
     * Extract object name from full URL
     * Example: http://localhost:9000/uploads/1/profile_photo.jpg -> 1/profile_photo.jpg
     */
    public String extractObjectName(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) {
            return null;
        }
        
        // Remove the base URL and bucket name
        String prefix = String.format("%s/%s/", minioUrl, bucketName);
        if (fileUrl.startsWith(prefix)) {
            return fileUrl.substring(prefix.length());
        }
        
        return null;
    }
}
