package at.ac.uibk.swa.models.annotations;

import at.ac.uibk.swa.models.Permission;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface AllPermission {
    Permission[] value();
}
