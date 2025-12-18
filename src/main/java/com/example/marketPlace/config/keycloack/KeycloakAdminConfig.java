package com.example.marketPlace.config.keycloack;

import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KeycloakAdminConfig {

    @Value("${keycloak.serverUrl}")
    private String  serverUrl;

    @Value("${keycloak.realm}")
    private String realmName;

    @Value("${keycloak.rootClient}")
    private String rootClient;

    @Value("${keycloak.clientSecret}")
    private String clientSecret;

    @Bean
    public Keycloak keycloakAdmin() {
        return KeycloakBuilder.builder()
                .serverUrl(serverUrl)
                .realm(realmName)
                .clientId(rootClient)
                .clientSecret(clientSecret)
                .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                .build();
    }
}
