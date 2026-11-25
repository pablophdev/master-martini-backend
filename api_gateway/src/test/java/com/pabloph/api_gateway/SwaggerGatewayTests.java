package com.pabloph.api_gateway;

import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SwaggerGatewayTests {
    // Simula servicios externos: comprueba el proxy real sin depender de sus bases de datos.
    private static final HttpServer upstream;
    private static final String SPEC = "{\"openapi\":\"3.1.0\",\"info\":{\"title\":\"Test\",\"version\":\"v1\"},\"paths\":{}}";
    static {
        try {
            upstream = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
            upstream.createContext("/v3/api-docs", exchange -> {
                byte[] body = SPEC.getBytes(StandardCharsets.UTF_8);
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(exchange.getRequestURI().getPath().equals("/v3/api-docs") ? 200 : 404, body.length);
                exchange.getResponseBody().write(body);
                exchange.close();
            });
            upstream.start();
        } catch (Exception e) { throw new ExceptionInInitializerError(e); }
    }

    @DynamicPropertySource
    static void services(DynamicPropertyRegistry registry) {
        for (String name : new String[]{"AUTH", "RECETAS", "CURSOS", "CONTACTO"}) {
            registry.add("MS_" + name + "_URL", () -> "http://127.0.0.1:" + upstream.getAddress().getPort());
        }
    }
    @LocalServerPort int port;
    private HttpResponse<String> get(String path) throws Exception {
        return HttpClient.newHttpClient().send(HttpRequest.newBuilder(URI.create("http://localhost:" + port + path)).GET().build(),
                HttpResponse.BodyHandlers.ofString());
    }
    @AfterAll static void stop() { upstream.stop(0); }

    @Test
    void exposesSwaggerAndProxiesAllFourSpecifications() throws Exception {
        assertEquals(200, get("/swagger-ui/index.html").statusCode());
        assertEquals(200, get("/swagger-ui/swagger-ui-bundle.js").statusCode());
        var config = get("/v3/api-docs/swagger-config");
        assertEquals(200, config.statusCode());
        for (String name : new String[]{"auth", "recetas", "cursos", "contacto"}) {
            assertTrue(config.body().contains("/v3/api-docs/" + name));
            var response = get("/v3/api-docs/" + name);
            assertEquals(200, response.statusCode(), name);
            assertEquals(SPEC, response.body(), name);
        }
        assertEquals(401, get("/api/auth/me").statusCode());
        assertEquals(401, get("/api/v1/admin/recipes").statusCode());
    }
}
