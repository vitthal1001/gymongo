package com.gymongo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class IntegrationSmokeTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate rest;

    @Test
    public void registerLoginAndListGyms() {
        String base = "http://localhost:" + port;

        // Register
        ResponseEntity<Map> reg = rest.postForEntity(base + "/api/auth/register",
            Map.of("username", "int_user", "password", "password", "fullName", "Int User", "email", "int@example.com"), Map.class);
        assertThat(reg.getStatusCode()).isEqualTo(HttpStatus.OK);

        // Login
        ResponseEntity<Map> login = rest.postForEntity(base + "/api/auth/login",
            Map.of("username", "int_user", "password", "password"), Map.class);
        assertThat(login.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(login.getBody()).containsKey("token");

        String token = (String) login.getBody().get("token");

        // Call protected endpoint
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<String> gyms = rest.exchange(base + "/api/gyms", org.springframework.http.HttpMethod.GET, entity, String.class);
        assertThat(gyms.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}
