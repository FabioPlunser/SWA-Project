package at.ac.uibk.swa.models.annotations.aspects;

import at.ac.uibk.swa.config.personAuthentication.AuthContext;
import at.ac.uibk.swa.models.Authenticable;
import at.ac.uibk.swa.models.Permission;
import at.ac.uibk.swa.models.annotations.HasPermission;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Aspect
@Component
public class HasPermissionAspect {
    @Around("@annotation(at.ac.uibk.swa.models.annotations.HasPermission)")
    public Object doSomething(ProceedingJoinPoint jp) throws Throwable {
        Set<Permission> requiredPermission = Arrays.stream(
                ((MethodSignature) jp.getSignature())
                    .getMethod()
                    .getAnnotation(HasPermission.class)
                        .value()
                ).collect(Collectors.toSet());

        Optional<Set<Permission>> maybeUserPermissions = AuthContext.getCurrentUser().map(Authenticable::getPermissions);
        if (maybeUserPermissions.isPresent()) {
            Set<Permission> userPermissions = maybeUserPermissions.get();
            for (Permission permission : requiredPermission) {
                if (userPermissions.contains(permission)) {
                    return jp.proceed();
                }
            }
        }

        throw new AccessDeniedException("");
    }
}
