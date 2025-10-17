package com.fliqo.jwt;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class JwtAuthorityConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    private final JwtProperties props;

    public JwtAuthorityConverter(JwtProperties props) {
        this.props = props;
    }

    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {

        String rolesClaim = Optional.ofNullable(props.headerInjection())
                .map(JwtProperties.HeaderInjection::rolesClaim)
                .filter(s -> !s.isBlank())
                .orElse(JwtClaimKeys.ROLES);

        Object rolesObj = jwt.getClaim(rolesClaim);

        Set<String> authorities = new LinkedHashSet<>(extractRoles(rolesObj));

        return authorities.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.collectingAndThen(Collectors.toList(), List::copyOf));
    }

    private Collection<String> extractRoles(Object rolesObj) {
        if (rolesObj == null) return List.of();

        List<String> tokens;

        if (rolesObj instanceof Collection<?> col) {
            tokens = col.stream().map(String::valueOf).toList();
        } else if (rolesObj instanceof String s) {
            tokens = Arrays.stream(s.split("[,\\s]+"))
                    .filter(t -> !t.isBlank())
                    .toList();
        } else {
            tokens = List.of();
        }

        return tokens.stream()
                .map(String::trim)
                .filter(t -> !t.isBlank())
                .map(this::ensureRolePrefix)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private String ensureRolePrefix(String role) {
        return role.startsWith("ROLE_") ? role : "ROLE_" + role;
    }
}
