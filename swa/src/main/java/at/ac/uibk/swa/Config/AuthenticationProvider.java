package at.ac.uibk.swa.Config;

import at.ac.uibk.swa.Models.Customer;
import at.ac.uibk.swa.Service.CustomerService;
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
    CustomerService loginService;

    @Override
    protected void additionalAuthenticationChecks(
            UserDetails userDetails,
            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken
    ) {}

    @Override
    protected UserDetails retrieveUser(
            String userName, UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken
    ) {
        // TODO: The Credentials should be the UUID returned by the AuthenticationFilter.
        // TODO: Also send a username and check that the Token is associated with the user?
        Object oToken = usernamePasswordAuthenticationToken.getCredentials();
        String sToken = oToken.toString();
        UUID token;

        try {
            token = UUID.fromString(sToken);
        } catch (Exception e) {
            throw new AuthenticationCredentialsNotFoundException("Invalid Token: " + sToken);
        }

        // Try to find the User with the given Session Token
        Optional<Customer> maybeCustomer = loginService.findByToken(token);
        if (maybeCustomer.isPresent()) {
            // If the Customer was found, successfully authenticate them by returning to the AuthenticationFilter.
            Customer customer = maybeCustomer.get();
            return new User(
                    customer.getUsername(), customer.getPasswdHash(),
                    true, true, true, true,
                    AuthorityUtils.createAuthorityList(customer.isAdmin() ? "ADMIN" : "USER")
            );
        }

        throw new AuthenticationCredentialsNotFoundException("Cannot find user with authentication token=" + sToken);
    }
}