package com.example.agrilinkback.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI agriLinkOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Agri-Link API")
                        .description("Backend API for the agricultural financing and sales platform.")
                        .version("0.0.1"));
    }
}
