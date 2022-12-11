package at.ac.uibk.swa.Models;

import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

public enum Permission {
    ANONYMOUS,
    USER,
    ADMIN;

    public static List<Permission> defaultPermissions() {
        return List.of(USER);
    }
}

