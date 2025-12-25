package com.abdelwahab.CampusCard;

import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

@Import(TestcontainersConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LoginControllerTest {
    
    @LocalServerPort
    Integer port;

    @BeforeEach
    void setup() {
        RestAssured.baseURI = "http://localhost:" + port;
        RestAssured.port = port;
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
            .body("token", notNullValue())
            .body("email", equalTo("test@eng.psu.edu.eg"))
            .body("role", equalTo("student"))
            .body("status", equalTo("APPROVED"))
            .body("message", containsString("successful"));
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
            .body("token", notNullValue())
            .body("email", equalTo("test@eng.psu.edu.eg"))
            .body("role", equalTo("student"))
            .body("status", equalTo("APPROVED"))
            .body("message", containsString("successful"));
    }

    @Test
    void testLogin_InvalidPassword_Failure() {
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
    void testLogin_UserNotFound_Failure() {
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

    @Test
    void testLogin_MissingPassword_Failure() {
        String requestBody = """
            {
                "identifier": "test@eng.psu.edu.eg"
            }
        """;

        RestAssured.given()
            .contentType(ContentType.JSON)
            .body(requestBody)
        .when()
            .post("/api/login")
        .then()
            // Spring Security returns 403 for validation errors on secured endpoints
            .statusCode(anyOf(equalTo(HttpStatus.BAD_REQUEST.value()), equalTo(HttpStatus.FORBIDDEN.value())));
    }

    @Test
    void testLogin_MissingIdentifier_Failure() {
        String requestBody = """
            {
                "password": "password123"
            }
        """;

        RestAssured.given()
            .contentType(ContentType.JSON)
            .body(requestBody)
        .when()
            .post("/api/login")
        .then()
            // Spring Security returns 403 for validation errors on secured endpoints
            .statusCode(anyOf(equalTo(HttpStatus.BAD_REQUEST.value()), equalTo(HttpStatus.FORBIDDEN.value())));
    }
}
