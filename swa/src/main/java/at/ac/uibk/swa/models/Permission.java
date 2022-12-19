package at.ac.uibk.swa.models;

import java.util.List;

public enum Permission {
    USER,
    ADMIN;

    public static List<Permission> defaultPermissions() {
        return List.of(USER);
    }
}

