package at.ac.uibk.swa.models.annotations;

import at.ac.uibk.swa.SwaApplication;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Operation(security = { @SecurityRequirement(name = SwaApplication.BEARER_KEY) })
public @interface BearerToken {
}
