package at.ac.uibk.swa.models.Annotations;


import org.springframework.security.access.prepost.PreAuthorize;

@PreAuthorize("hasAuthority('USER')")
public @interface User {
}
