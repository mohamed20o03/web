/**
 * File storage domain using MinIO object storage.
 * Handles upload, retrieval, and deletion of user files and images.
 *
 * <p>Key components:
 * <ul>
 *   <li><strong>Service:</strong> MinioService for object storage operations</li>
 *   <li><strong>Configuration:</strong> MinioConfig for client setup</li>
 *   <li><strong>DTOs:</strong> File upload responses (profile photos, national ID scans)</li>
 * </ul>
 *
 * <p>Supported file types:
 * <ul>
 *   <li><strong>Profile Photos:</strong> User profile pictures (JPEG/PNG, max 10MB)</li>
 *   <li><strong>National ID Scans:</strong> Identity verification documents (JPEG/PNG, max 10MB)</li>
 * </ul>
 *
 * <p>Storage structure:
 * <pre>
 * bucket-name/
 *   ├── profile-photos/
 *   │   ├── {userId}.jpg
 *   │   └── {userId}.png
 *   └── national-ids/
 *       ├── {userId}.jpg
 *       └── {userId}.png
 * </pre>
 *
 * <p>File lifecycle:
 * <ol>
 *   <li>User uploads file via multipart form data</li>
 *   <li>Service validates file type and size</li>
 *   <li>File uploaded to MinIO with unique name</li>
 *   <li>Public URL returned and stored in database</li>
 *   <li>File accessible via URL for authorized users</li>
 *   <li>Deleted when user account deleted</li>
 * </ol>
 *
 * @since 1.0
 * @author CampusCard Team
 */
package com.abdelwahab.CampusCard.domain.storage;
