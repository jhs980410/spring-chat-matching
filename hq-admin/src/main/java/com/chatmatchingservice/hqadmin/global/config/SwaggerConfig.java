package com.chatmatchingservice.hqadmin.global.config;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@SecurityScheme(
        name = "BearerAuth",           // Swagger에서 명시적으로 사용할 이름
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",             // 반드시 소문자 bearer
        bearerFormat = "JWT"           // 표시용
)
public class SwaggerConfig {

    @Bean
    public OpenAPI chatMatchingAPI() {

        // 🔥 모든 API 요청에 BearerAuth 적용하도록 SecurityRequirement 추가
        SecurityRequirement securityRequirement = new SecurityRequirement()
                .addList("BearerAuth");

        return new OpenAPI()
                .info(new Info()
                        .title("Chat Matching API")
                        .description("실시간 상담 매칭 서비스 API 문서")
                        .version("v1.0")
                )
                .addSecurityItem(securityRequirement)   // 🔥 Authorization 헤더 사용하도록 설정
                .components(new Components());          // 🔥 SecurityScheme 활성화를 위한 Components 필수
    }
}
