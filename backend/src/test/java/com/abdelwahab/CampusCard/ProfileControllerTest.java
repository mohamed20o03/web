package com.abdelwahab.CampusCard;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

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
public class ProfileControllerTest {

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
    void shouldGetCurrentUserProfile() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/profile"))
                .header("Authorization", "Bearer " + authToken)
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        assertEquals(200, response.statusCode());
        JsonNode json = objectMapper.readTree(response.body());
        assertEquals("test@eng.psu.edu.eg", json.get("email").asText());
        assertNotNull(json.get("firstName"));
        assertNotNull(json.get("lastName"));
        assertEquals("student", json.get("role").asText());
        assertNotNull(json.get("faculty"));
        assertNotNull(json.get("department"));
    }

    @Test
    void shouldFailToGetProfileWithoutAuthentication() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/profile"))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        // Returns 404 because without authentication, the controller cannot identify which user's profile to retrieve
        assertEquals(404, response.statusCode());
    }

    @Test
    void shouldGetAnotherUserProfileWithPublicVisibility() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/profile/1"))
                .header("Authorization", "Bearer " + authToken)
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        assertEquals(200, response.statusCode());
        JsonNode json = objectMapper.readTree(response.body());
        assertEquals(1, json.get("userId").asInt());
        assertNotNull(json.get("email"));
    }

    @Test
    void shouldFailToGetNonExistentUserProfile() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/profile/99999"))
                .header("Authorization", "Bearer " + authToken)
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        // Should be 404 (not found) or 403 (forbidden)
        assertTrue(response.statusCode() == 404 || response.statusCode() == 403);
    }

    @Test
    void shouldUpdateCurrentUserProfile() throws Exception {
        String updateRequest = """
            {
                "bio": "This is my updated bio",
                "phone": "+1234567890",
                "linkedin": "https://linkedin.com/in/testuser",
                "github": "https://github.com/testuser",
                "interests": "Java, Spring Boot, Testing",
                "visibility": "PUBLIC"
            }
            """;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/profile"))
                .header("Authorization", "Bearer " + authToken)
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(updateRequest))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        assertEquals(200, response.statusCode());
        JsonNode json = objectMapper.readTree(response.body());
        assertEquals("This is my updated bio", json.get("bio").asText());
        assertEquals("+1234567890", json.get("phone").asText());
        assertEquals("https://linkedin.com/in/testuser", json.get("linkedin").asText());
        assertEquals("https://github.com/testuser", json.get("github").asText());
        assertEquals("Java, Spring Boot, Testing", json.get("interests").asText());
    }

    @Test
    void shouldPartiallyUpdateProfile() throws Exception {
        String updateRequest = """
            {
                "bio": "Only updating bio"
            }
            """;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/profile"))
                .header("Authorization", "Bearer " + authToken)
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(updateRequest))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        assertEquals(200, response.statusCode());
        JsonNode json = objectMapper.readTree(response.body());
        assertEquals("Only updating bio", json.get("bio").asText());
    }

    @Test
    void shouldFailToUpdateProfileWithInvalidPhone() throws Exception {
        String updateRequest = """
            {
                "phone": "invalid-phone"
            }
            """;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/profile"))
                .header("Authorization", "Bearer " + authToken)
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(updateRequest))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        assertEquals(400, response.statusCode());
    }

    @Test
    void shouldFailToUpdateProfileWithInvalidLinkedIn() throws Exception {
        String updateRequest = """
            {
                "linkedin": "https://twitter.com/testuser"
            }
            """;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/profile"))
                .header("Authorization", "Bearer " + authToken)
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(updateRequest))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        assertEquals(400, response.statusCode());
    }

    @Test
    void shouldFailToUpdateProfileWithInvalidGitHub() throws Exception {
        String updateRequest = """
            {
                "github": "https://gitlab.com/testuser"
            }
            """;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/profile"))
                .header("Authorization", "Bearer " + authToken)
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(updateRequest))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        assertEquals(400, response.statusCode());
    }

    @Test
    void shouldFailToUpdateProfileWithInvalidVisibility() throws Exception {
        String updateRequest = """
            {
                "visibility": "INVALID_VISIBILITY"
            }
            """;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/profile"))
                .header("Authorization", "Bearer " + authToken)
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(updateRequest))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        assertEquals(400, response.statusCode());
    }

    @Test
    void shouldChangeVisibilityToPrivate() throws Exception {
        String updateRequest = """
            {
                "visibility": "PRIVATE"
            }
            """;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/profile"))
                .header("Authorization", "Bearer " + authToken)
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(updateRequest))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        assertEquals(200, response.statusCode());
    }

    @Test
    void shouldFailToUpdateProfileWithoutAuthentication() throws Exception {
        String updateRequest = """
            {
                "bio": "Unauthorized Update"
            }
            """;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/profile"))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(updateRequest))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        // Returns 400 because the endpoint validates authentication and rejects invalid requests
        assertEquals(400, response.statusCode());
    }

    @Test
    void shouldFailToViewPendingUserProfile() throws Exception {
        // Try to view a pending user's profile (assuming user ID 3 is pending from V4 migration)
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/profile/3"))
                .header("Authorization", "Bearer " + authToken)
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        // Should be 403 (forbidden) - pending users are not visible to others
        assertEquals(403, response.statusCode(), "Pending users should not be visible to other students");
    }

    @Test
    void shouldFailToViewRejectedUserProfile() throws Exception {
        // Try to view a rejected user's profile (assuming user ID 4 is rejected from V4 migration)
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/profile/4"))
                .header("Authorization", "Bearer " + authToken)
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        // Should be 403 (forbidden) - rejected users are not visible to others
        assertEquals(403, response.statusCode(), "Rejected users should not be visible to other students");
    }

    @Test
    void shouldGetPublicStudents() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/profile/public-students"))
                .header("Authorization", "Bearer " + authToken)
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        assertEquals(200, response.statusCode(), "Should successfully get public students");
        JsonNode json = objectMapper.readTree(response.body());
        assertTrue(json.isArray(), "Response should be an array");
        
        // Verify that only approved students with public visibility are returned
        for (JsonNode profile : json) {
            assertEquals("student", profile.get("role").asText(), "All returned profiles should be students");
            assertEquals("public", profile.get("visibility").asText(), "All returned profiles should have public visibility");
            assertNotNull(profile.get("userId"), "Profile should have userId");
            assertNotNull(profile.get("email"), "Profile should have email");
            assertNotNull(profile.get("firstName"), "Profile should have firstName");
            assertNotNull(profile.get("lastName"), "Profile should have lastName");
        }
    }

    @Test
    void shouldUpdateVisibilityToPrivate() throws Exception {
        String updateRequest = """
            {
                "visibility": "PRIVATE"
            }
            """;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/profile/visibility"))
                .header("Authorization", "Bearer " + authToken)
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(updateRequest))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        assertEquals(200, response.statusCode(), "Should successfully update visibility to PRIVATE");
        JsonNode json = objectMapper.readTree(response.body());
        assertEquals("private", json.get("visibility").asText(), "Visibility should be updated to private");
    }

    @Test
    void shouldUpdateVisibilityToPublic() throws Exception {
        String updateRequest = """
            {
                "visibility": "PUBLIC"
            }
            """;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/profile/visibility"))
                .header("Authorization", "Bearer " + authToken)
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(updateRequest))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        assertEquals(200, response.statusCode(), "Should successfully update visibility to PUBLIC");
        JsonNode json = objectMapper.readTree(response.body());
        assertEquals("public", json.get("visibility").asText(), "Visibility should be updated to public");
    }

    @Test
    void shouldFailToUpdateVisibilityWithInvalidValue() throws Exception {
        String updateRequest = """
            {
                "visibility": "INVALID"
            }
            """;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/profile/visibility"))
                .header("Authorization", "Bearer " + authToken)
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(updateRequest))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        assertEquals(400, response.statusCode(), "Should fail with invalid visibility value");
    }

    @Test
    void shouldNotReturnPrivateProfilesInPublicStudents() throws Exception {
        // First, set user profile to PRIVATE
        String updateRequest = """
            {
                "visibility": "PRIVATE"
            }
            """;

        HttpRequest updateVisibilityRequest = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/profile/visibility"))
                .header("Authorization", "Bearer " + authToken)
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(updateRequest))
                .build();

        HttpResponse<String> updateResponse = httpClient.send(updateVisibilityRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, updateResponse.statusCode());

        // Now check if the user appears in public-students list
        HttpRequest getPublicStudentsRequest = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/profile/public-students"))
                .header("Authorization", "Bearer " + authToken)
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(getPublicStudentsRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        
        JsonNode json = objectMapper.readTree(response.body());
        JsonNode currentProfile = objectMapper.readTree(updateResponse.body());
        int currentUserId = currentProfile.get("userId").asInt();
        
        // Verify that the current user (now private) does NOT appear in the list
        for (JsonNode profile : json) {
            assertTrue(profile.get("userId").asInt() != currentUserId, 
                "Private profile should not appear in public students list");
        }

        // Restore visibility to PUBLIC for other tests
        String restoreRequest = """
            {
                "visibility": "PUBLIC"
            }
            """;

        HttpRequest restoreVisibilityRequest = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/profile/visibility"))
                .header("Authorization", "Bearer " + authToken)
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(restoreRequest))
                .build();

        httpClient.send(restoreVisibilityRequest, HttpResponse.BodyHandlers.ofString());
    }
}
