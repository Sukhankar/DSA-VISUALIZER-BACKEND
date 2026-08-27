package com.codeloom.dsa.common.config;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer"
)
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("DSA Visualizer API")
                        .version("v1")
                        .description("REST API for the DSA Visualizer platform. Supports authentication, algorithm discovery, visualization step generation, administration, favorites, and learning progress tracking.")
                        .contact(new Contact()
                                .name("CodeLoom Team")
                                .email("contact@codeloom.com")
                        )
                );
    }
}
