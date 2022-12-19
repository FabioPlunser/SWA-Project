package at.ac.uibk.swa.config;

import at.ac.uibk.swa.util.UUIDConversionUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.io.IOException;
import java.util.Optional;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

/**
 * <p>
 * Class responsible for getting the Authentication Token from the Request.
 * </p>
 * <p>
 * The Token is the then passed onto the AuthenticationProvider.
 * </p>
 *
 * @see AbstractAuthenticationProcessingFilter
 */
public class BearerTokenAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    public BearerTokenAuthenticationFilter(final RequestMatcher requiresAuth) {
        super(requiresAuth);
    }

    @Override
    public Authentication attemptAuthentication(
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse
    ) throws AuthenticationException {
        Optional<UsernamePasswordAuthenticationToken> authenticationToken =
                // Get the Authorization Header
                Optional.ofNullable(httpServletRequest.getHeader(AUTHORIZATION))
                        // Remove the
                        .map(header -> header.substring("Bearer".length()).trim())
                        // Try to convert the Token given in the Header to a UUID
                        .map(UUIDConversionUtil::tryConvertUUID)
                        // If the Token is a valid UUID then pass it onto the AuthenticationFilter
                        .map(token -> new UsernamePasswordAuthenticationToken(null, token));

        // If a Cookie-Token was found, pass it to the AuthenticationManager/AuthenticationProvider.
        if (authenticationToken.isPresent()) {
            return getAuthenticationManager().authenticate(authenticationToken.get());
        }

        throw new AuthenticationCredentialsNotFoundException("No Token was sent with the Request!");
    }

    @Override
    protected void successfulAuthentication(
            final HttpServletRequest request, final HttpServletResponse response,
            final FilterChain chain, final Authentication authResult
    ) throws IOException, ServletException {
        // If the user was successfully authenticated, store it in the Security Context.
        SecurityContextHolder.getContext().setAuthentication(authResult);
        // Continue running the Web Security Filter Chain.
        chain.doFilter(request, response);
    }
}