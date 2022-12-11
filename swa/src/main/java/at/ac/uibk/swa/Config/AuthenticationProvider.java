package at.ac.uibk.swa.Config;

import at.ac.uibk.swa.Models.Person;
import at.ac.uibk.swa.Service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

/**
 * <p>
 *     Class responsible for checking that the Session Token exists for the
 *     given User.
 * </p>
 * <p>
 *     The Token is provided by the AuthenticationFilter.
 * </p>
 *
 * @see AuthenticationFilter
 */
@Component
public class AuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {

    @Autowired
    PersonService loginService;

    @Override
    protected void additionalAuthenticationChecks(
            UserDetails userDetails,
            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken
    ) {}

    @Override
    protected UserDetails retrieveUser(
            String userName, UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken
    ) {
        UUID token = (UUID) usernamePasswordAuthenticationToken.getCredentials();

        // Try to find the User with the given Session Token
        Optional<Person> maybePerson = loginService.findByToken(token);
        if (maybePerson.isPresent()) {
            // If the Customer was found, successfully authenticate them by returning to the AuthenticationFilter.
            Person person = maybePerson.get();
            usernamePasswordAuthenticationToken.setDetails(person);
            return new User(
                    person.getUsername(), person.getPassword(),
                    true, true, true, true,
                    AuthorityUtils.createAuthorityList(person
                            .getPermissions().stream().map(x -> x.toString()).toArray(String[]::new))
            );
        }

        throw new AuthenticationCredentialsNotFoundException("Cannot find user with authentication token=" + token.toString());
    }
}