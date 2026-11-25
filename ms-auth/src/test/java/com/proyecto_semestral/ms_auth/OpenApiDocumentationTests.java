package com.proyecto_semestral.ms_auth;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashSet;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OpenApiDocumentationTests {
    @LocalServerPort int port;
    @Autowired RequestMappingHandlerMapping requestMappingHandlerMapping;
    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper json = new ObjectMapper();

    private HttpResponse<String> get(String path) throws Exception {
        return client.send(HttpRequest.newBuilder(URI.create("http://localhost:" + port + path)).GET().build(),
                HttpResponse.BodyHandlers.ofString());
    }

    @Test
    void documentsEveryControllerOperationAndServesSwaggerWithoutAuthentication() throws Exception {
        var response = get("/v3/api-docs");
        assertEquals(200, response.statusCode(), response.body());
        var spec = json.readTree(response.body());
        assertTrue(spec.path("openapi").asText().startsWith("3."));
        assertEquals("bearer", spec.at("/components/securitySchemes/bearerAuth/scheme").asText());
        assertEquals("/", spec.at("/servers/0/url").asText());
        var ids = new HashSet<String>();
        int operations = 0;
        for (var entry : requestMappingHandlerMapping.getHandlerMethods().entrySet()) {
            if (!entry.getValue().getBeanType().getPackageName().equals("com.proyecto_semestral.ms_auth.controller")) continue;
            for (String path : entry.getKey().getPatternValues()) {
                for (var method : entry.getKey().getMethodsCondition().getMethods()) {
                    JsonNode op = spec.path("paths").path(path).path(method.name().toLowerCase());
                    assertFalse(op.isMissingNode(), method + " " + path);
                    assertFalse(op.path("summary").asText().isBlank(), path);
                    assertTrue(ids.add(op.path("operationId").asText()), path);
                    assertTrue(op.path("responses").properties().stream().anyMatch(e -> e.getKey().startsWith("2")), path);
                    if (op.has("parameters")) {
                        for (var parameter : op.path("parameters")) assertNotEquals("jwt", parameter.path("name").asText());
                    }
                    operations++;
                }
            }
        }
        assertEquals(6, operations);
        assertEquals(200, get("/v3/api-docs.yaml").statusCode());
        assertEquals(200, get("/v3/api-docs/swagger-config").statusCode());
        assertEquals(200, get("/swagger-ui/index.html").statusCode());
        assertEquals(200, get("/swagger-ui/swagger-ui-bundle.js").statusCode());
        assertEquals(401, get("/api/auth/me").statusCode());
    }

    private String adminToken() { return tokenWithRole("ADMIN"); }

    private String tokenWithRole(String role) {
        var key = new javax.crypto.spec.SecretKeySpec(java.util.Base64.getDecoder().decode(secret), "HmacSHA256");
        var encoder = org.springframework.security.oauth2.jwt.NimbusJwtEncoder.withSecretKey(key)
                .algorithm(org.springframework.security.oauth2.jose.jws.MacAlgorithm.HS256).build();
        var claims = org.springframework.security.oauth2.jwt.JwtClaimsSet.builder().issuer("ms-auth")
                .subject("swagger-test@example.com").issuedAt(java.time.Instant.now())
                .expiresAt(java.time.Instant.now().plusSeconds(300)).claim("roles", java.util.List.of(role)).build();
        return encoder.encode(org.springframework.security.oauth2.jwt.JwtEncoderParameters.from(
                org.springframework.security.oauth2.jwt.JwsHeader.with(
                        org.springframework.security.oauth2.jose.jws.MacAlgorithm.HS256).build(), claims)).getTokenValue();
    }
    @org.springframework.beans.factory.annotation.Value("${security.jwt.secret}") String secret;

    private JsonNode call(String method, String path, String body, String token, int status) throws Exception {
        var builder = HttpRequest.newBuilder(URI.create("http://localhost:" + port + path))
                .header("Content-Type", "application/json");
        if (token != null) builder.header("Authorization", "Bearer " + token);
        var response = client.send(builder.method(method, HttpRequest.BodyPublishers.ofString(body)).build(),
                HttpResponse.BodyHandlers.ofString());
        assertEquals(status, response.statusCode(), method + " " + path + " " + response.body());
        return response.body().isBlank() ? json.createObjectNode() : json.readTree(response.body());
    }
    private void image(String upload, String download, String token) throws Exception {
        String boundary = "SwaggerTestBoundary";
        String body = "--" + boundary + "\r\nContent-Disposition: form-data; name=\"image\"; filename=\"test.png\"\r\n"
                + "Content-Type: image/png\r\n\r\nexample-image-bytes\r\n--" + boundary + "--\r\n";
        var response = client.send(HttpRequest.newBuilder(URI.create("http://localhost:" + port + upload))
                .header("Authorization", "Bearer " + token).header("Content-Type", "multipart/form-data; boundary=" + boundary)
                .POST(HttpRequest.BodyPublishers.ofString(body)).build(), HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), response.body());
        var downloaded = get(download);
        assertEquals(200, downloaded.statusCode());
        assertEquals("image/png", downloaded.headers().firstValue("Content-Type").orElse(""));
        assertEquals("example-image-bytes", downloaded.body());
    }

    @Test
    void registrationLoginProfileAndPhotoWorkOverHttp() throws Exception {
        String registration = "{\"nombre\":\"Usuario de prueba\",\"email\":\"swagger@example.com\",\"password\":\"EjemploSeguro2026\"}";
        call("POST", "/api/auth/register", "{}", null, 400);
        var registered = call("POST", "/api/auth/register", registration, null, 201);
        call("POST", "/api/auth/register", registration, null, 409);
        call("POST", "/api/auth/login", "{\"email\":\"swagger@example.com\",\"password\":\"incorrecta\"}", null, 401);
        var login = call("POST", "/api/auth/login", "{\"email\":\"swagger@example.com\",\"password\":\"EjemploSeguro2026\"}", null, 200);
        String token = login.path("accessToken").asText();
        assertFalse(token.isBlank());
        assertEquals("swagger@example.com", call("GET", "/api/auth/me", "", token, 200).path("email").asText());
        assertEquals("Actualizado", call("PUT", "/api/auth/perfil", "{\"nombre\":\"Actualizado\"}", token, 200).path("nombre").asText());
        image("/api/auth/perfil/foto", "/imagenes/perfiles/" + registered.path("usuario").path("id").asLong(), token);
    }
}
