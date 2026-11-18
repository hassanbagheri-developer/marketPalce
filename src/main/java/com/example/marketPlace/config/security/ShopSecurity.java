package com.example.marketPlace.config.security;

/**
 * Client role created in Keycloak for the shop admin.
 * A user with only this role can sign in and call {@code /api/admin/shop/**}.
 */
public final class ShopSecurity {

    public static final String MANAGER = "manager";
    public static final String HAS_MANAGER = "hasAuthority('manager')";

    private ShopSecurity() {
    }
}
