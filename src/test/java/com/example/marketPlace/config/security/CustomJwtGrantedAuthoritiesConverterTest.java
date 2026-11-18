package com.example.marketPlace.config.security;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CustomJwtGrantedAuthoritiesConverterTest {

    private final CustomJwtGrantedAuthoritiesConverter converter =
            new CustomJwtGrantedAuthoritiesConverter("marketPlace");

    @Test
    void grantsManagerFromMarketplaceClient() {
        Jwt jwt = token(Map.of(), Map.of("marketPlace", Map.of("roles", List.of("manager"))));

        assertEquals(List.of("manager"), authorities(jwt));
    }

    @Test
    void ignoresManagerFromRealmAndOtherClientsWhilePreservingExistingRoles() {
        Jwt jwt = token(
                Map.of("roles", List.of("manager", "realm-user")),
                Map.of(
                        "other-client", Map.of("roles", List.of("manager")),
                        "marketPlace", Map.of("roles", List.of("api.marketplace.api.test1"))
                )
        );

        assertEquals(List.of("realm-user", "api.marketplace.api.test1"), authorities(jwt));
    }

    @Test
    void grantsNoAuthorityWhenTokenHasNoRoleClaims() {
        Jwt jwt = Jwt.withTokenValue("test-token").header("alg", "RS256").subject("user").build();

        assertEquals(List.of(), authorities(jwt));
    }

    private List<String> authorities(Jwt jwt) {
        return converter.convert(jwt).stream().map(GrantedAuthority::getAuthority).toList();
    }

    private Jwt token(Map<String, ?> realmAccess, Map<String, ?> resourceAccess) {
        return Jwt.withTokenValue("test-token")
                .header("alg", "RS256")
                .subject("user")
                .claim("realm_access", realmAccess)
                .claim("resource_access", resourceAccess)
                .build();
    }
}
