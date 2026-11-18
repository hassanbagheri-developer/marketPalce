package com.example.marketPlace.config.keycloack;

import com.example.marketPlace.config.security.ShopSecurity;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.RoleRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KeycloakRoleService {

    private final Keycloak keycloak;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.clientId}")
    private String clientId;

    public void createClientRoleIfNotExists(String roleName) {

        var realmResource = keycloak.realm(realm);
        var clients = realmResource.clients().findByClientId(clientId);

        if (clients.isEmpty()) {
            throw new IllegalStateException("Client not found in Keycloak: " + clientId);
        }

        var client = clients.get(0);
        var rolesResource = realmResource.clients().get(client.getId()).roles();

        try {
            rolesResource.get(roleName).toRepresentation();
            log.debug("Role already exists: {}", roleName);
        } catch (NotFoundException e) {
            RoleRepresentation role = new RoleRepresentation();
            role.setName(roleName);
            if (ShopSecurity.MANAGER.equals(roleName)) {
                role.setDescription("Shop manager: sign in and call shop admin methods only");
            }
            rolesResource.create(role);
            log.info("🆕 Role created in Keycloak: {}", roleName);
        }
    }
}
