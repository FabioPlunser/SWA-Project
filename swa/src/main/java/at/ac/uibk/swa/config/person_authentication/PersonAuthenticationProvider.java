package at.ac.uibk.swa.config.person_authentication;

import at.ac.uibk.swa.models.Authenticable;
import at.ac.uibk.swa.models.Permission;
import at.ac.uibk.swa.models.Person;
import at.ac.uibk.swa.models.annotations.AllPermission;
import at.ac.uibk.swa.models.annotations.AnyPermission;
import at.ac.uibk.swa.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.GrantedAuthority;
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
    private PersonService loginService;

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
        return maybePerson.orElseThrow(() -> new BadCredentialsException(formatTokenError(token)));
    }

    private static String formatTokenError(UUID token) {
        return String.format("Cannot find user with authentication token: <%s>", token.toString());
    }
}