package at.ac.uibk.swa.config.person_authentication;

import at.ac.uibk.swa.models.Authenticable;
import at.ac.uibk.swa.models.Permission;
import at.ac.uibk.swa.models.Person;
import at.ac.uibk.swa.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

/**
 * Class responsible for checking that the Session Token exists for the
 * given User.
 * <br/>
 * The Token is provided by the {@link jakarta.servlet.Filter}.
 *
 * @author David Rieser
 * @see AbstractUserDetailsAuthenticationProvider
 * @see org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter
 */
@Component("personAuthenticationProvider")
public class PersonAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {

    @Autowired
    PersonService loginService;

    @Override
    protected void additionalAuthenticationChecks(
            UserDetails userDetails,
            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken
    ) {
        // All Checks are done in retrieveUser
    }

    @Override
    protected UserDetails retrieveUser(
            String userName,
            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken
    ) {
        UUID token = (UUID) usernamePasswordAuthenticationToken.getCredentials();

        // Try to find the User with the given Session Token
        Optional<Person> maybePerson = loginService.findByToken(token);
        return maybePerson.orElseThrow(
                () -> new BadCredentialsException(
                        String.format("Cannot find user with authentication token: <%s>", token.toString())
                )
        );
    }

    /**
     * Convert the Permissions of an Authenticable to Strings, so the Permission for the Endpoints can be checked
     * using the PreAuthorize-Annotation.
     *
     * @param authenticable The User whose Permissions should be converted.
     * @return A List of the User's Permissions as Strings.
     * @see org.springframework.security.access.prepost.PreAuthorize
     * @see Permission
     * @see at.ac.uibk.swa.models.annotations.HasPermission
     */
    private static Collection<GrantedAuthority> getAuthorities(Authenticable authenticable) {
        return authenticable.getPermissions();
    }
}