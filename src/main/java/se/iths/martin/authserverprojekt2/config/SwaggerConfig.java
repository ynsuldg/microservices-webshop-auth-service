package se.iths.martin.authserverprojekt2.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Konfigurerar API-dokumentation och JWT Bearer-autentisering i Swagger.
 */
@Configuration
public class SwaggerConfig {
    /**
     * Tillåter authentication med token för auth-service. Swagger-configuration.
     *
     * @return OpenAPI-konfiguration med JWT Bearer-autentisering
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .addSecurityItem(new SecurityRequirement().addList("authorization"))
                .components(new Components()
                        .addSecuritySchemes("authorization",
                                new SecurityScheme()
                                        .name("authorization")
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .in(SecurityScheme.In.HEADER)));
    }
}