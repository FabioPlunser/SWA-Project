package at.ac.uibk.swa.models.annotations;

import org.springframework.security.access.prepost.PreAuthorize;

/**
 * Helper-Annotation for Controller-Endpoints to check if the User has ADMIN-{@link at.ac.uibk.swa.models.Permission}.
 */
@PreAuthorize("hasAuthority(T(at.ac.uibk.swa.models.Permission).USER.toString())")
public @interface User {
}
