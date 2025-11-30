package com.fliqo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.HttpStatusServerEntryPoint;
import org.springframework.security.web.server.authorization.HttpStatusServerAccessDeniedHandler;

import com.fliqo.jwt.JwtAuthorityConverter;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(
            ServerHttpSecurity http,
            ReactiveJwtDecoder jwtDecoder,
            JwtAuthorityConverter authorityConverter) {
        http.authorizeExchange(
                reg ->
                        reg.pathMatchers(HttpMethod.OPTIONS, "/**")
                                .permitAll()
                                .pathMatchers("/actuator/**")
                                .permitAll()
                                .pathMatchers("/api/auth/**")
                                .permitAll()
                                .pathMatchers(
                                        HttpMethod.POST,
                                        "/api/member/login",
                                        "/api/member/signup",
                                        "/api/member/email-check",
                                        "/api/member/phone/**",
                                        "/api/member/password/reset/**",
                                        "/api/member/email/find/**")
                                .permitAll()
                                .pathMatchers("/swagger-ui.html", "/swagger-ui/**")
                                .permitAll()
                                .pathMatchers("/v3/api-docs/**")
                                .permitAll()
                                .anyExchange()
                                .authenticated());

        http.csrf(ServerHttpSecurity.CsrfSpec::disable);

        http.exceptionHandling(
                e ->
                        e.authenticationEntryPoint(
                                        new HttpStatusServerEntryPoint(HttpStatus.UNAUTHORIZED))
                                .accessDeniedHandler(
                                        new HttpStatusServerAccessDeniedHandler(
                                                HttpStatus.FORBIDDEN)));

        http.oauth2ResourceServer(
                oauth2 ->
                        oauth2.jwt(
                                jwtSpec -> {
                                    jwtSpec.jwtDecoder(jwtDecoder);
                                    JwtAuthenticationConverter delegate =
                                            new JwtAuthenticationConverter();
                                    delegate.setJwtGrantedAuthoritiesConverter(
                                            jwt -> authorityConverter.convert(jwt));

                                    jwtSpec.jwtAuthenticationConverter(
                                            new ReactiveJwtAuthenticationConverterAdapter(
                                                    delegate));
                                }));

        return http.build();
    }
}
