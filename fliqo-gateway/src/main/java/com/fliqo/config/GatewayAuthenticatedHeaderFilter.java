package com.fliqo.config;

import static com.fliqo.jwt.JwtClaimKeys.SUB;

import java.util.stream.Collectors;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.fliqo.jwt.HeaderKeys;
import com.fliqo.jwt.JwtProperties;

import reactor.core.publisher.Mono;

@Component
public class GatewayAuthenticatedHeaderFilter implements GlobalFilter, Ordered {

    private final JwtProperties props;

    public GatewayAuthenticatedHeaderFilter(JwtProperties props) {
        this.props = props;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        if (path.startsWith("/v3/api-docs") || path.startsWith("/swagger-ui")) {
            return chain.filter(exchange);
        }

        if (props.headerInjection() == null || !props.headerInjection().enabled()) {
            return chain.filter(exchange);
        }

        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .filter(auth -> auth != null && auth.isAuthenticated())
                .flatMap(
                        auth -> {
                            if (auth == null || !auth.isAuthenticated()) {
                                return chain.filter(exchange);
                            }

                            String userIdClaim =
                                    props.headerInjection().userIdClaim() != null
                                            ? props.headerInjection().userIdClaim()
                                            : SUB;

                            final String userId;
                            if (auth instanceof JwtAuthenticationToken jwtAuth) {
                                Object claimValue = jwtAuth.getToken().getClaim(userIdClaim);
                                if (claimValue != null) {
                                    userId = String.valueOf(claimValue);
                                } else {
                                    userId = jwtAuth.getName();
                                }
                            } else {
                                userId = auth.getName();
                            }

                            final String rolesCsv =
                                    auth.getAuthorities().stream()
                                            .map(GrantedAuthority::getAuthority)
                                            .collect(Collectors.joining(","));

                            ServerHttpRequest mutated =
                                    exchange.getRequest()
                                            .mutate()
                                            .headers(
                                                    h -> {
                                                        if (userId != null && !userId.isBlank()) {
                                                            h.set(HeaderKeys.USER_ID, userId);
                                                        }
                                                        if (!rolesCsv.isBlank()) {
                                                            h.set(HeaderKeys.ROLES_CSV, rolesCsv);
                                                        }
                                                    })
                                            .build();

                            return chain.filter(exchange.mutate().request(mutated).build());
                        })
                .switchIfEmpty(chain.filter(exchange));
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }
}
