package com.abdelwahab.CampusCard;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

@Import(TestcontainersConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class UserControllerTest {
    
    @LocalServerPort
    Integer port;

    @BeforeEach
    void setup() {
        RestAssured.baseURI = "http://localhost:" + port;
        RestAssured.port = port;
    }
    
    @Test
    void testUserCreation_Success() {
        // Create a simple test image file (1x1 pixel)
        byte[] imageBytes = new byte[] {
            (byte)0xFF, (byte)0xD8, (byte)0xFF, (byte)0xE0, 0x00, 0x10, 0x4A, 0x46,
            0x49, 0x46, 0x00, 0x01, 0x01, 0x00, 0x00, 0x01, 0x00, 0x01, 0x00, 0x00,
            (byte)0xFF, (byte)0xDB, 0x00, 0x43, 0x00, 0x08, 0x06, 0x06, 0x07, 0x06,
            0x05, 0x08, 0x07, 0x07, 0x07, 0x09, 0x09, 0x08, 0x0A, 0x0C, 0x14, 0x0D,
            0x0C, 0x0B, 0x0B, 0x0C, 0x19, 0x12, 0x13, 0x0F, 0x14, 0x1D, 0x1A, 0x1F,
            0x1E, 0x1D, 0x1A, 0x1C, 0x1C, 0x20, 0x24, 0x2E, 0x27, 0x20, 0x22, 0x2C,
            0x23, 0x1C, 0x1C, 0x28, 0x37, 0x29, 0x2C, 0x30, 0x31, 0x34, 0x34, 0x34,
            0x1F, 0x27, 0x39, 0x3D, 0x38, 0x32, 0x3C, 0x2E, 0x33, 0x34, 0x32,
            (byte)0xFF, (byte)0xC0, 0x00, 0x0B, 0x08, 0x00, 0x01, 0x00, 0x01, 0x01,
            0x01, 0x11, 0x00, (byte)0xFF, (byte)0xC4, 0x00, 0x14, 0x00, 0x01, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x03, (byte)0xFF, (byte)0xC4, 0x00, 0x14, 0x10, 0x01, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, (byte)0xFF, (byte)0xDA, 0x00, 0x08, 0x01, 0x01, 0x00,
            0x00, 0x3F, 0x00, 0x37, (byte)0xFF, (byte)0xD9
        };

        RestAssured.given()
            .multiPart("email", "newuser@eng.psu.edu.eg")
            .multiPart("password", "password123")
            .multiPart("firstName", "New")
            .multiPart("lastName", "User")
            .multiPart("dateOfBirth", "2000-01-01")
            .multiPart("nationalId", "11223344556677")  // Use unique ID for UserController
            .multiPart("nationalIdScan", "national_id.jpg", imageBytes, "image/jpeg")
            .multiPart("year", "2")
            .multiPart("facultyId", "1")
            .multiPart("departmentId", "1")
        .when()
            .post("/api/signup")
        .then()
            .statusCode(HttpStatus.CREATED.value())
            .body("email", equalTo("newuser@eng.psu.edu.eg"))
            .body("status", equalTo("PENDING"))
            .body("message", containsString("registered successfully"))
            .body("id", notNullValue());
    }

    @Test
    void testLogin_WithEmail_Success() {
        String requestBody = """
            {
                "identifier": "test@eng.psu.edu.eg",
                "password": "password123"
            }
        """;

        RestAssured.given()
            .contentType(ContentType.JSON)
            .body(requestBody)
        .when()
            .post("/api/login")
        .then()
            .statusCode(HttpStatus.OK.value())
            .body("email", equalTo("test@eng.psu.edu.eg"))
            .body("role", equalTo("student"))
            .body("status", equalTo("APPROVED"))
            .body("token", notNullValue())
            .body("message", equalTo("Login successful"));
    }

    @Test
    void testLogin_WithNationalId_Success() {
        String requestBody = """
            {
                "identifier": "12345678901234",
                "password": "password123"
            }
        """;

        RestAssured.given()
            .contentType(ContentType.JSON)
            .body(requestBody)
        .when()
            .post("/api/login")
        .then()
            .statusCode(HttpStatus.OK.value())
            .body("email", equalTo("test@eng.psu.edu.eg"))
            .body("token", notNullValue());
    }

    @Test
    void testLogin_InvalidCredentials() {
        String requestBody = """
            {
                "identifier": "test@eng.psu.edu.eg",
                "password": "wrongpassword"
            }
        """;

        RestAssured.given()
            .contentType(ContentType.JSON)
            .body(requestBody)
        .when()
            .post("/api/login")
        .then()
            .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    void testLogin_UserNotFound() {
        String requestBody = """
            {
                "identifier": "nonexistent@eng.psu.edu.eg",
                "password": "password123"
            }
        """;

        RestAssured.given()
            .contentType(ContentType.JSON)
            .body(requestBody)
        .when()
            .post("/api/login")
        .then()
            .statusCode(HttpStatus.UNAUTHORIZED.value());
    }
   
}