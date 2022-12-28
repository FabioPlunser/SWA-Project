package at.ac.uibk.swa.util;

import at.ac.uibk.swa.config.personAuthentication.AuthContext;
import at.ac.uibk.swa.models.Authenticable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

public class MockAuthContext {
    /**
     * Sets the currently logged in user
     * Logs out any other user that is currently logged in
     *
     * @param user user to be logged in
     * @return logged in user if successfull, otherwise null
     */
    public static Authenticable setLoggedInUser(Authenticable user) {
        SecurityContextHolder.setContext(new SecurityContext() {
            @Override
            public Authentication getAuthentication() {
                UsernamePasswordAuthenticationToken token =  new UsernamePasswordAuthenticationToken(null, UUID.randomUUID());
                token.setDetails(user);
                return token;
            }

            @Override
            public void setAuthentication(Authentication authentication) {

            }
        });
        if (AuthContext.getCurrentUser().isEmpty()) {
            return null;
        } else {
            return AuthContext.getCurrentUser().get();
        }
    }
}
