package com.fliqo.jwt;

import io.jsonwebtoken.io.Decoders;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;


import javax.crypto.spec.SecretKeySpec;
import java.util.Objects;

@Configuration
public class JwtDecoderConfig {
    private final JwtProperties props;

    public JwtDecoderConfig(JwtProperties props) {
        this.props = props;
    }

    @Bean
    public ReactiveJwtDecoder reactiveJwtDecoder() {
        byte[] keyBytes = Decoders.BASE64.decode(props.secret());
        var secretKey = new SecretKeySpec(keyBytes, "HmacSHA256");

        NimbusReactiveJwtDecoder decoder = NimbusReactiveJwtDecoder
                .withSecretKey(secretKey)
                .macAlgorithm(MacAlgorithm.HS256)
                .build();

        OAuth2TokenValidator<Jwt> withIssuer =
                JwtValidators.createDefaultWithIssuer(props.issuer());

        OAuth2TokenValidator<Jwt> validator = getJwtOAuth2TokenValidator(withIssuer);
        decoder.setJwtValidator(validator);

        return decoder;
    }

    private OAuth2TokenValidator<Jwt> getJwtOAuth2TokenValidator(OAuth2TokenValidator<Jwt> withIssuer) {
        OAuth2TokenValidator<Jwt> withAudience = token -> {
            var audiences = token.getAudience();
            if (audiences == null || audiences.stream().noneMatch(a -> Objects.equals(a, props.audience()))) {
                return OAuth2TokenValidatorResult.failure(
                        new OAuth2Error(
                                "invalid_token",
                                "필수 audience 값이 누락되었습니다: " + props.audience(),
                                null
                        )
                );
            }
            return OAuth2TokenValidatorResult.success();
        };

        return new DelegatingOAuth2TokenValidator<>(
                withIssuer, withAudience
        );
    }
}
