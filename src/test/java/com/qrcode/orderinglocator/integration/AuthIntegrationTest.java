package com.qrcode.orderinglocator.integration;

import com.qrcode.orderinglocator.dto.auth.AuthResponse;
import com.qrcode.orderinglocator.dto.auth.LoginRequest;
import com.qrcode.orderinglocator.dto.auth.RegisterRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class AuthIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @BeforeEach
    void setUp() {
        // Wait for the application to be fully started with retry logic
        int maxRetries = 30;
        int retryCount = 0;
        boolean serverReady = false;
        
        while (retryCount < maxRetries && !serverReady) {
            try {
                ResponseEntity<String> healthResponse = restTemplate.getForEntity(
                    "http://localhost:" + port + "/actuator/health", String.class);
                if (healthResponse.getStatusCode().is2xxSuccessful()) {
                    serverReady = true;
                }
            } catch (Exception e) {
                retryCount++;
                try {
                    Thread.sleep(500); // Wait 500ms between retries
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
        
        if (!serverReady) {
            throw new RuntimeException("Server failed to start within expected time");
        }
    }

    @Test
    void register_ValidRequest_ReturnsCreatedWithToken() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setName("John Doe");
        request.setEmail("john.doe@example.com");
        request.setPhone("+1234567890");
        request.setPassword("Password@123");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<RegisterRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<AuthResponse> response = restTemplate.postForEntity(
                "http://localhost:" + port + "/api/auth/register",
                entity,
                AuthResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getToken()).isNotEmpty();
        assertThat(response.getBody().getEmail()).isEqualTo("john.doe@example.com");
        assertThat(response.getBody().getName()).isEqualTo("John Doe");
        assertThat(response.getBody().getRole().toString()).isEqualTo("CUSTOMER");
        assertThat(response.getBody().getId()).isNotNull();
    }

    @Test
    void login_ValidCredentials_ReturnsOkWithToken() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail("admin@qrlocator.com");
        request.setPassword("Admin@123");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<LoginRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<AuthResponse> response = restTemplate.postForEntity(
                "http://localhost:" + port + "/api/auth/login",
                entity,
                AuthResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getToken()).isNotEmpty();
        assertThat(response.getBody().getEmail()).isEqualTo("admin@qrlocator.com");
        assertThat(response.getBody().getRole().toString()).isEqualTo("ADMIN");
    }

    @Test
    void login_InvalidCredentials_ReturnsUnauthorized() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail("admin@qrlocator.com");
        request.setPassword("WrongPassword");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<LoginRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(
                "http://localhost:" + port + "/api/auth/login",
                entity,
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).contains("Invalid email or password");
    }
}