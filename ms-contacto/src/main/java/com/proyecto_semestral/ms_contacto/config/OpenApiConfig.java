package com.proyecto_semestral.ms_contacto.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info().title("Master Martini — ms-contacto").version("v1")
                        .description("API de ms-contacto. Las operaciones privadas requieren un JWT emitido por ms-auth. "
                                + "En Authorize ingresa solo accessToken. Los permisos se indican en cada operación."))
                .servers(java.util.List.of(new io.swagger.v3.oas.models.servers.Server().url("/")))
                .components(new Components().addSecuritySchemes("bearerAuth",
                        new SecurityScheme().type(SecurityScheme.Type.HTTP)
                                .scheme("bearer").bearerFormat("JWT")));
    }
}
