package com.ProfileService.configuration;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

public class CustomAuthoritiesConverter implements Converter<Jwt, Collection<GrantedAuthority>> {
    @Override
    public Collection<GrantedAuthority> convert(Jwt source) {
        Map<String, Object> realmAccess = source.getClaimAsMap("realm_access");
        Object roles = realmAccess.get("roles");
        if (roles instanceof List rolesList) {
            return ((List<String>) rolesList)
                    .stream().map(s -> new SimpleGrantedAuthority("ROLE_" + s)).collect(Collectors.toList());
        }

        return List.of();
    }
}
