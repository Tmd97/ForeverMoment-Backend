package com.forvmom.core.config;

import org.springdoc.core.models.GroupedOpenApi;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

        private static final String SECURITY_SCHEME_NAME = "bearerAuth";

        @Bean
        public GroupedOpenApi publicApi() {
                return GroupedOpenApi.builder()
                                .group("1-public")
                                .pathsToMatch("/public/**", "/auth/**")
                                .displayName("Public API")
                                .build();
        }

        @Bean
        public GroupedOpenApi userApi() {
                return GroupedOpenApi.builder()
                                .group("2-user")
                                .pathsToMatch("/user/**")
                                .displayName("User API")
                                .build();
        }

        @Bean
        public GroupedOpenApi adminApi() {
                return GroupedOpenApi.builder()
                                .group("3-admin")
                                .pathsToMatch("/admin/**")
                                .displayName("Admin API")
                                .build();
        }

        @Bean
        public OpenAPI customOpenAPI() {
                return new OpenAPI()
                                .info(apiInfo())
                                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
                                .components(new Components()
                                                .addSecuritySchemes(SECURITY_SCHEME_NAME, createSecurityScheme()));
        }

        private SecurityScheme createSecurityScheme() {
                return new SecurityScheme()
                                .name(SECURITY_SCHEME_NAME)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .in(SecurityScheme.In.HEADER)
                                .description("Enter JWT token (without 'Bearer' prefix)");
        }

        private Info apiInfo() {
                return new Info()
                                .title("MomentForever Platform API")
                                .description("REST API documentation for the MomentForever Application.\n\n" +
                                                "**Groups:**\n" +
                                                "- **Public API**: Accessible without authentication (e.g., login, registration, browsing).\n"
                                                +
                                                "- **User API**: Requires 'User' role authentication.\n" +
                                                "- **Admin API**: Requires 'Admin' role authentication.")
                                .version("1.0.0")
                                .contact(new Contact()
                                                .name("API Support")
                                                .email("support@forvmom.com"))
                                .license(new License()
                                                .name("Apache 2.0")
                                                .url("https://www.apache.org/licenses/LICENSE-2.0"));
        }
}