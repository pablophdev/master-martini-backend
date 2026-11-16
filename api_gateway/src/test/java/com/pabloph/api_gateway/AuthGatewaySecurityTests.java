package com.pabloph.api_gateway;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.List;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.sun.net.httpserver.HttpServer;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class AuthGatewaySecurityTests {

    private static final SecretKey JWT_KEY = createKey();
    private static final HttpServer AUTH_UPSTREAM = startUpstream();

    @Value("${local.server.port}")
    private int port;

    private WebTestClient webTestClient;

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add(
                "security.jwt.secret",
                () -> Base64.getEncoder().encodeToString(JWT_KEY.getEncoded()));
        registry.add(
                "MS_AUTH_URL",
                () -> "http://localhost:" + AUTH_UPSTREAM.getAddress().getPort());
    }

    @AfterAll
    static void stopUpstream() {
        AUTH_UPSTREAM.stop(0);
    }

    @BeforeEach
    void setUpClient() {
        webTestClient = WebTestClient.bindToServer()
                .baseUrl("http://localhost:" + port)
                .build();
    }

    @Test
    void registerYLoginSonPublicos() {
        webTestClient.post()
                .uri("/api/auth/register")
                .bodyValue("{}")
                .exchange()
                .expectStatus().isOk();

        webTestClient.post()
                .uri("/api/auth/login")
                .bodyValue("{}")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void meExigeBearerValido() {
        webTestClient.get()
                .uri("/api/auth/me")
                .exchange()
                .expectStatus().isUnauthorized();

        webTestClient.get()
                .uri("/api/auth/me")
                .headers(headers -> headers.setBearerAuth(token("otro-emisor", Instant.now().plusSeconds(60))))
                .exchange()
                .expectStatus().isUnauthorized();

        webTestClient.get()
                .uri("/api/auth/me")
                .headers(headers -> headers.setBearerAuth(token("ms-auth", Instant.now().minusSeconds(60))))
                .exchange()
                .expectStatus().isUnauthorized();

        webTestClient.get()
                .uri("/api/auth/me")
                .headers(headers -> headers.setBearerAuth(token("ms-auth", Instant.now().plusSeconds(60))))
                .exchange()
                .expectStatus().isOk();
    }

    private static String token(String issuer, Instant expiresAt) {
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(issuer)
                .subject("ana@example.com")
                .issuedAt(now.minusSeconds(120))
                .expiresAt(expiresAt)
                .claim("roles", List.of("USER"))
                .build();
        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).type("JWT").build();
        return NimbusJwtEncoder.withSecretKey(JWT_KEY)
                .algorithm(MacAlgorithm.HS256)
                .build()
                .encode(JwtEncoderParameters.from(header, claims))
                .getTokenValue();
    }

    private static SecretKey createKey() {
        try {
            KeyGenerator generator = KeyGenerator.getInstance("HmacSHA256");
            generator.init(256);
            return generator.generateKey();
        } catch (Exception exception) {
            throw new IllegalStateException(exception);
        }
    }

    private static HttpServer startUpstream() {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress("localhost", 0), 0);
            server.createContext("/", exchange -> {
                byte[] body = "{}".getBytes(StandardCharsets.UTF_8);
                exchange.getResponseHeaders().add("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, body.length);
                exchange.getResponseBody().write(body);
                exchange.close();
            });
            server.start();
            return server;
        } catch (IOException exception) {
            throw new IllegalStateException(exception);
        }
    }
}
