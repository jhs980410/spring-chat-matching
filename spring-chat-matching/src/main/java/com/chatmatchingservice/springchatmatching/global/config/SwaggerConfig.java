package com.chatmatchingservice.springchatmatching.global.config;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@SecurityScheme(
        name = "BearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"
)
public class SwaggerConfig {

    @Bean
    public OpenAPI springChatMatchingAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Chat Matching API")
                        .description("실시간 상담 매칭 서비스 API")
                        .version("v1.0"));
    }
}
