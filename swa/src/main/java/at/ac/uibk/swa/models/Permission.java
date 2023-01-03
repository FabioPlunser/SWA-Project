package at.ac.uibk.swa.models;

import org.springframework.security.core.GrantedAuthority;

import java.util.Set;

public enum Permission implements GrantedAuthority {
    USER,
    ADMIN;

    public static Set<Permission> defaultPermissions() {
        return Set.of(USER);
    }

    public static Set<GrantedAuthority> defaultAuthorities() {
        return Set.of(USER);
    }

    @Override
    public String getAuthority() {
        return this.toString();
    }
}

