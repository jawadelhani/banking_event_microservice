package com.jawad.bank.agency.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// Custom converter that transforms a raw JWT: "roles": [ "ADMIN","CLIENT"]
//into a Spring Security ROLE_ADMIN ,ROLE_CLIENT
public class JwtAuthConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {

        JwtAuthenticationConverter jwtConverter = new JwtAuthenticationConverter();

        jwtConverter.setJwtGrantedAuthoritiesConverter(source -> {

            // "realm_access" looks like: { "roles": ["ADMIN", "USER"] }
            Map<String, Object> realmAccess = source.getClaim("realm_access");

            // No roles present in the token -> no authorities granted
            if (realmAccess == null || !realmAccess.containsKey("roles")) {
                return List.of();
            }

            // Extract the raw role names from the claim
            List<String> roles = (List<String>) realmAccess.get("roles");

            // Convert each role into a Spring Security authority.
            Collection<GrantedAuthority> authorities =
                    roles.stream()
                            .map(role -> (GrantedAuthority) new SimpleGrantedAuthority("ROLE_" + role))
                            .collect(Collectors.toList());

            return authorities;
        });

        return jwtConverter.convert(jwt);
    }
}