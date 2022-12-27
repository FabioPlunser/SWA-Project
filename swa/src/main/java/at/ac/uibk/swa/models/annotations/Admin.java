package at.ac.uibk.swa.models.annotations;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.*;

/**
 * Helper-Annotation for Controller-Endpoints to check if the User has ADMIN-{@link at.ac.uibk.swa.models.Permission}
 */
@Target({ElementType.METHOD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@PreAuthorize("hasAuthority(T(at.ac.uibk.swa.models.Permission).ADMIN.toString())")
public @interface Admin {
}
