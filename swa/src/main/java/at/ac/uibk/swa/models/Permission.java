package at.ac.uibk.swa.models;

import java.util.Set;

public enum Permission {
    USER,
    ADMIN;

    public static Set<Permission> defaultPermissions() {
        return Set.of(USER);
    }
}

