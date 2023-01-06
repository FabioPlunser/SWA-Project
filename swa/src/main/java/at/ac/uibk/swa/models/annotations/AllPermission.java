package at.ac.uibk.swa.models.annotations;

import at.ac.uibk.swa.models.Permission;
import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.*;

@Inherited
@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface AllPermission {
    Permission[] value();
}
