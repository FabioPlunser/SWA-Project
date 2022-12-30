package at.ac.uibk.swa.config.person_authentication;

import at.ac.uibk.swa.models.Authenticable;
import at.ac.uibk.swa.models.Permission;
import at.ac.uibk.swa.models.Person;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;
import java.util.UUID;

/**
 * Helper Class for accessing security Information contained in the current request.
 *
 * @author David Rieser
 */
public class AuthContext {

    // All your constructors are belong to us!
    private AuthContext() {}

    /**
     * Check whether the User that made the current Request is authenticated.
     * A User is authenticated if he is logged-in with Username and Password (and in turn with his Token.
     * Otherwise, the Request is anonymous.
     *
     * @return true if a logged-in user made the request, false if the request is anonymous or if the Authentication failed.
     */
    public static boolean isAuthenticated() {
        return !(SecurityContextHolder.getContext().getAuthentication() instanceof AnonymousAuthenticationToken);
    }

    /**
     * Helper Method for accessing the current Authentication Token.
     * Currently, the only supported AuthenticationToken is a UsernamePasswordAuthenticationToken.
     *
     * @return The AuthenticationToken sent with the current request (if a valid one was sent).
     * @see UsernamePasswordAuthenticationToken
     */
    private static Optional<UsernamePasswordAuthenticationToken> getAuthentication() {
        if (!isAuthenticated())
            return Optional.empty();
        return Optional.of((UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication());
    }

    /**
     * Get the user that is currently logged-in.
     *
     * @return The Authenticable whose credentials were sent with the request (if valid credentials were sent).
     * @see Authenticable
     */
    public static Optional<Authenticable> getCurrentUser() {
        return getAuthentication().map(x -> (Authenticable) x.getDetails());
    }

    /**
     * Get the person that is currently logged-in.
     *
     * @return The Person whose credentials were sent with the request (if valid credentials were sent).
     * @see Person
     */
    public static Optional<Person> getCurrentPerson() {
        return Optional.ofNullable(getCurrentUser().orElse(null) instanceof Person p ? p : null);
    }

    /**
     * Get the Token that was sent with the request (either as a Bearer-, or Cookie-Token).
     *
     * @return The Token sent with the request (if a valid one was sent).
     */
    public static Optional<UUID> getLoginToken() {
        return getAuthentication().map(x -> ((Authenticable) x.getDetails()).getToken());
    }

    /**
     * Checks whether the currently logged-in user has the required Permission.
     * This method also works if the user is not logged-in or anonymous (and will return false in those cases).
     *
     * @param required The Permission that the User needs to have.
     * @return true if the user has the required Permissions, false otherwise.
     */
    public static boolean hasPermission(Permission required) {
        return getCurrentUser()
                .map(Authenticable::getPermissions)
                .map(permissions -> permissions.contains(required))
                .orElse(false);
    }
}
