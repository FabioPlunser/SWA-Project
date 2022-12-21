package at.ac.uibk.swa.config;

import at.ac.uibk.swa.models.Authenticable;
import at.ac.uibk.swa.models.Permission;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;
import java.util.UUID;

public class AuthContext {

    private AuthContext() {}

    public static boolean isAuthenticated() {
        return !(SecurityContextHolder.getContext().getAuthentication() instanceof AnonymousAuthenticationToken);
    }

    private static Optional<UsernamePasswordAuthenticationToken> getAuthentication() {
        if (!isAuthenticated())
            return Optional.empty();
        return Optional.of((UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication());
    }

    public static Optional<Authenticable> getCurrentUser() {
        return getAuthentication().map(x -> (Authenticable) x.getDetails());
    }

    public static Optional<UUID> getLoginToken() {
        return getAuthentication().map(x -> ((Authenticable) x.getDetails()).getToken());
    }

    public static boolean hasPermission(Permission required) {
        return getCurrentUser()
                .map(Authenticable::getPermissions)
                .map(permissions -> permissions.contains(required))
                .orElse(false);
    }
}
