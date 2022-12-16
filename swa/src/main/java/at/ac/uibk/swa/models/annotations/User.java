package at.ac.uibk.swa.models.annotations;


import org.springframework.security.access.prepost.PreAuthorize;

@PreAuthorize("hasAuthority('USER')")
public @interface User {
}
