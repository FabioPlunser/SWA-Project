package at.ac.uibk.swa.Models;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public enum Permission {
    ANONYMOUS,
    USER,
    ADMIN,
}

