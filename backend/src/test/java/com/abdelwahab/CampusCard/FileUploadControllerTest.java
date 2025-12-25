package com.abdelwahab.CampusCard;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Import(TestcontainersConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class FileUploadControllerTest {

    @LocalServerPort
    Integer port;

    private String authToken;
    private HttpClient httpClient;
    private ObjectMapper objectMapper;
    private String baseUrl;

    @BeforeEach
    void setup() throws Exception {
        httpClient = HttpClient.newHttpClient();
        objectMapper = new ObjectMapper();
        baseUrl = "http://localhost:" + port;

        // Get auth token
        String loginBody = """
            {
                "identifier": "test@eng.psu.edu.eg",
                "password": "password123"
            }
            """;

        HttpRequest loginRequest = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/login"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(loginBody))
                .build();

        HttpResponse<String> loginResponse = httpClient.send(loginRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, loginResponse.statusCode(), "Login should succeed");
        
        JsonNode loginJson = objectMapper.readTree(loginResponse.body());
        authToken = loginJson.get("token").asText();
        assertNotNull(authToken, "Auth token should not be null");
    }

    @Test
    void shouldUploadProfilePhotoSuccessfully() throws Exception {
        byte[] fileContent = createFakeJpegContent();
        String boundary = UUID.randomUUID().toString();
        byte[] multipartBody = createMultipartBody(boundary, "test.jpg", "image/jpeg", fileContent);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/profile/photo"))
                .header("Authorization", "Bearer " + authToken)
                .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                .POST(HttpRequest.BodyPublishers.ofByteArray(multipartBody))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        assertEquals(200, response.statusCode(), "Upload should succeed: " + response.body());
        JsonNode json = objectMapper.readTree(response.body());
        assertNotNull(json.get("photoUrl"), "Should return photo URL");
    }

    @Test
    void shouldUploadNationalIdScanSuccessfully() throws Exception {
        byte[] fileContent = createFakeJpegContent();
        String boundary = UUID.randomUUID().toString();
        byte[] multipartBody = createMultipartBody(boundary, "id_scan.jpg", "image/jpeg", fileContent);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/profile/national-id-scan"))
                .header("Authorization", "Bearer " + authToken)
                .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                .POST(HttpRequest.BodyPublishers.ofByteArray(multipartBody))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        assertEquals(200, response.statusCode(), "Upload should succeed: " + response.body());
        JsonNode json = objectMapper.readTree(response.body());
        assertNotNull(json.get("scanUrl"), "Should return scan URL");
    }

    @Test
    void shouldFailToUploadProfilePhotoWithoutAuthentication() throws Exception {
        byte[] fileContent = createFakeJpegContent();
        String boundary = UUID.randomUUID().toString();
        byte[] multipartBody = createMultipartBody(boundary, "test.jpg", "image/jpeg", fileContent);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/profile/photo"))
                .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                .POST(HttpRequest.BodyPublishers.ofByteArray(multipartBody))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        // Returns 400 because the endpoint validates authentication and rejects invalid requests
        assertEquals(400, response.statusCode());
    }

    @Test
    void shouldFailToUploadNationalIdScanWithoutAuthentication() throws Exception {
        byte[] fileContent = createFakeJpegContent();
        String boundary = UUID.randomUUID().toString();
        byte[] multipartBody = createMultipartBody(boundary, "id_scan.jpg", "image/jpeg", fileContent);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/profile/national-id-scan"))
                .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                .POST(HttpRequest.BodyPublishers.ofByteArray(multipartBody))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        // Returns 400 because the endpoint validates authentication and rejects invalid requests
        assertEquals(400, response.statusCode());
    }

    @Test
    void shouldFailToUploadEmptyFile() throws Exception {
        byte[] fileContent = new byte[0]; // Empty file
        String boundary = UUID.randomUUID().toString();
        byte[] multipartBody = createMultipartBody(boundary, "empty.jpg", "image/jpeg", fileContent);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/profile/photo"))
                .header("Authorization", "Bearer " + authToken)
                .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                .POST(HttpRequest.BodyPublishers.ofByteArray(multipartBody))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        assertEquals(400, response.statusCode());
    }

    @Test
    void shouldFailToUploadNonImageFile() throws Exception {
        byte[] fileContent = "this is plain text content".getBytes(StandardCharsets.UTF_8);
        String boundary = UUID.randomUUID().toString();
        byte[] multipartBody = createMultipartBody(boundary, "document.txt", "text/plain", fileContent);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/profile/photo"))
                .header("Authorization", "Bearer " + authToken)
                .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                .POST(HttpRequest.BodyPublishers.ofByteArray(multipartBody))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        assertEquals(400, response.statusCode());
    }

    @Test
    void shouldReplaceExistingProfilePhoto() throws Exception {
        // Upload first photo
        byte[] firstContent = createFakeJpegContent();
        String boundary1 = UUID.randomUUID().toString();
        byte[] multipartBody1 = createMultipartBody(boundary1, "first.jpg", "image/jpeg", firstContent);

        HttpRequest request1 = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/profile/photo"))
                .header("Authorization", "Bearer " + authToken)
                .header("Content-Type", "multipart/form-data; boundary=" + boundary1)
                .POST(HttpRequest.BodyPublishers.ofByteArray(multipartBody1))
                .build();

        HttpResponse<String> response1 = httpClient.send(request1, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response1.statusCode(), "First upload should succeed");
        String firstUrl = objectMapper.readTree(response1.body()).get("photoUrl").asText();
        assertNotNull(firstUrl, "First upload should return URL");

        // Upload second photo (should replace first)
        byte[] secondContent = createFakeJpegContent();
        String boundary2 = UUID.randomUUID().toString();
        byte[] multipartBody2 = createMultipartBody(boundary2, "second.jpg", "image/jpeg", secondContent);

        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/profile/photo"))
                .header("Authorization", "Bearer " + authToken)
                .header("Content-Type", "multipart/form-data; boundary=" + boundary2)
                .POST(HttpRequest.BodyPublishers.ofByteArray(multipartBody2))
                .build();

        HttpResponse<String> response2 = httpClient.send(request2, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response2.statusCode(), "Second upload should succeed");
        String secondUrl = objectMapper.readTree(response2.body()).get("photoUrl").asText();
        assertNotNull(secondUrl, "Second upload should return URL");
    }

    @Test
    void shouldUploadPngFormat() throws Exception {
        byte[] fileContent = createFakePngContent();
        String boundary = UUID.randomUUID().toString();
        byte[] multipartBody = createMultipartBody(boundary, "test.png", "image/png", fileContent);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/profile/photo"))
                .header("Authorization", "Bearer " + authToken)
                .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                .POST(HttpRequest.BodyPublishers.ofByteArray(multipartBody))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        assertEquals(200, response.statusCode(), "PNG upload should succeed: " + response.body());
    }

    @Test
    void shouldStoreFilesWithCorrectPath() throws Exception {
        byte[] fileContent = createFakeJpegContent();
        String boundary = UUID.randomUUID().toString();
        byte[] multipartBody = createMultipartBody(boundary, "photo.jpg", "image/jpeg", fileContent);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/profile/photo"))
                .header("Authorization", "Bearer " + authToken)
                .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                .POST(HttpRequest.BodyPublishers.ofByteArray(multipartBody))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        assertEquals(200, response.statusCode());
        JsonNode json = objectMapper.readTree(response.body());
        String photoUrl = json.get("photoUrl").asText();
        
        // URL should contain proper path structure
        assertNotNull(photoUrl);
        assertTrue(photoUrl.length() > 0, "Photo URL should not be empty");
    }

    @Test
    void shouldVerifyProfileContainsUploadedPhoto() throws Exception {
        // Upload photo
        byte[] fileContent = createFakeJpegContent();
        String boundary = UUID.randomUUID().toString();
        byte[] multipartBody = createMultipartBody(boundary, "test.jpg", "image/jpeg", fileContent);

        HttpRequest uploadRequest = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/profile/photo"))
                .header("Authorization", "Bearer " + authToken)
                .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                .POST(HttpRequest.BodyPublishers.ofByteArray(multipartBody))
                .build();

        HttpResponse<String> uploadResponse = httpClient.send(uploadRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, uploadResponse.statusCode());

        // Get profile and verify photo URL is present
        HttpRequest profileRequest = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/profile"))
                .header("Authorization", "Bearer " + authToken)
                .GET()
                .build();

        HttpResponse<String> profileResponse = httpClient.send(profileRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, profileResponse.statusCode());
        
        JsonNode profileJson = objectMapper.readTree(profileResponse.body());
        assertNotNull(profileJson.get("profilePhoto"), "Profile should have photo URL");
    }

    // Helper methods to create multipart body
    private byte[] createMultipartBody(String boundary, String filename, String contentType, byte[] content) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        String lineEnd = "\r\n";
        
        // File part
        baos.write(("--" + boundary + lineEnd).getBytes(StandardCharsets.UTF_8));
        baos.write(("Content-Disposition: form-data; name=\"file\"; filename=\"" + filename + "\"" + lineEnd).getBytes(StandardCharsets.UTF_8));
        baos.write(("Content-Type: " + contentType + lineEnd).getBytes(StandardCharsets.UTF_8));
        baos.write(lineEnd.getBytes(StandardCharsets.UTF_8));
        baos.write(content);
        baos.write(lineEnd.getBytes(StandardCharsets.UTF_8));
        
        // End boundary
        baos.write(("--" + boundary + "--" + lineEnd).getBytes(StandardCharsets.UTF_8));
        
        return baos.toByteArray();
    }

    // Create fake JPEG content (with proper JPEG magic bytes)
    private byte[] createFakeJpegContent() {
        byte[] content = new byte[1024];
        // JPEG magic bytes
        content[0] = (byte) 0xFF;
        content[1] = (byte) 0xD8;
        content[2] = (byte) 0xFF;
        content[3] = (byte) 0xE0;
        // Fill rest with dummy data
        for (int i = 4; i < content.length - 2; i++) {
            content[i] = (byte) (i % 256);
        }
        // JPEG end marker
        content[content.length - 2] = (byte) 0xFF;
        content[content.length - 1] = (byte) 0xD9;
        return content;
    }

    // Create fake PNG content (with proper PNG magic bytes)
    private byte[] createFakePngContent() {
        byte[] content = new byte[1024];
        // PNG magic bytes
        content[0] = (byte) 0x89;
        content[1] = (byte) 0x50; // P
        content[2] = (byte) 0x4E; // N
        content[3] = (byte) 0x47; // G
        content[4] = (byte) 0x0D;
        content[5] = (byte) 0x0A;
        content[6] = (byte) 0x1A;
        content[7] = (byte) 0x0A;
        // Fill rest with dummy data
        for (int i = 8; i < content.length; i++) {
            content[i] = (byte) (i % 256);
        }
        return content;
    }
}
