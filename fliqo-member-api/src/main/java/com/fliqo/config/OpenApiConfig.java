package com.fliqo.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        // 보안 스키마 이름
        final String SCHEME = "bearerAuth";

        return new OpenAPI()
                // 게이트웨이를 경유해 호출되도록 서버 URL을 gateway로 지정 (로컬 기준)
                .servers(List.of(new Server().url("http://localhost:8080").description("via gateway")))
                // Bearer JWT 스키마 등록
                .components(new Components().addSecuritySchemes(
                        SCHEME,
                        new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                ))
                // 전역으로 보안 요구 추가(각 API에 자동 적용)
                .addSecurityItem(new SecurityRequirement().addList(SCHEME));
    }
}