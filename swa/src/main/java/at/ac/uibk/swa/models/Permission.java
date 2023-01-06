package at.ac.uibk.swa.models;

import org.springframework.security.core.GrantedAuthority;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public enum Permission implements GrantedAuthority {
    USER,
    ADMIN;

    public static Set<Permission> defaultPermissions() {
        return Set.of(USER);
    }

    public static Set<GrantedAuthority> defaultAuthorities() {
        return Set.of(USER);
    }

    public static Set<Permission> adminPermissions() {
        return Set.of(ADMIN);
    }

    public static Set<GrantedAuthority> adminAuthorities() {
        return Set.of(ADMIN);
    }

    private final static Set<Permission> ALL_PERMISSIONS = Arrays.stream(Permission.values()).collect(Collectors.toSet());

    public static Set<Permission> allPermissions() {
        return (Set) ALL_PERMISSIONS;
    }

    public static Set<GrantedAuthority> allAuthorities() {
        return (Set) ALL_PERMISSIONS;
    }

    @Override
    public String getAuthority() {
        return this.toString();
    }
}

