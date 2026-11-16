package com.pabloph.api_gateway;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.net.URI;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.core.env.Environment;

@SpringBootTest(properties = {
		"security.jwt.secret=MDEyMzQ1Njc4OTAxMjM0NTY3ODkwMTIzNDU2Nzg5MDE="
})
class ApiGatewayApplicationTests {

	@Autowired
	private Environment environment;

	@Autowired
	private RouteDefinitionLocator routeDefinitionLocator;

	@Test
	void contextLoads() {
	}

	@Test
	void listensForAndroidClientsOnGatewayPort() {
		assertEquals("8083", environment.getProperty("server.port"));
		assertEquals("0.0.0.0", environment.getProperty("server.address"));
	}

	@Test
	void exposesRoutesForAllBackendServices() {
		Map<String, URI> routes = routeDefinitionLocator.getRouteDefinitions()
				.collectList()
				.block()
				.stream()
				.collect(Collectors.toMap(RouteDefinition::getId, RouteDefinition::getUri));

		assertEquals(URI.create("http://localhost:8080"), routes.get("ms-recetas"));
		assertEquals(URI.create("http://localhost:8081"), routes.get("ms-cursos"));
		assertEquals(URI.create("http://localhost:8082"), routes.get("ms-contacto"));
		assertEquals(URI.create("http://localhost:8084"), routes.get("ms-auth"));
	}
}
