package com.notes.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI notesOpenAPI() {
        return new OpenAPI().info(new Info()
                .title("Notes Management API")
                .description("REST API for creating, reading, updating, and deleting notes.")
                .version("v1")
                .contact(new Contact()
                        .name("Notes Team")
                        .email("support@example.com"))
                .license(new License()
                        .name("MIT")
                        .url("https://opensource.org/licenses/MIT")));
    }
}
