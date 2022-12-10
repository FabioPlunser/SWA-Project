package at.ac.uibk.swa.Config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

/**
 * <p>
 * Class responsible for getting the Authentication Token from the Request.
 * </p>
 * <p>
 * The Token is the then passed onto the AuthenticationProvider.
 * </p>
 *
 * @see at.ac.uibk.swa.Config.AuthenticationProvider
 * @see AbstractAuthenticationProcessingFilter
 */
public class AuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    AuthenticationFilter(final RequestMatcher requiresAuth) {
        super(requiresAuth);
    }

    @Override
    public Authentication attemptAuthentication(
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse
    ) throws AuthenticationException
    {
        // Get the Authorization Header
        Optional<String> authHeader = Optional.ofNullable(httpServletRequest.getHeader(AUTHORIZATION));

        // Check that an Authorization Header was sent.
        if (authHeader.isPresent()) {
            // Get the Token from the Header.
            String bearerToken = authHeader.get().substring("Bearer".length()).trim();
            UUID token;
            // Try to parse the Header
            try {
                token = UUID.fromString(bearerToken);
            } catch (Exception e) {
                throw new BadCredentialsException("Malformed Token");
            }

            // If the Token is a valid UUID then pass it onto the AuthenticationFilter as a Credential
            UsernamePasswordAuthenticationToken requestAuthentication = new UsernamePasswordAuthenticationToken(null, token);
            return getAuthenticationManager().authenticate(requestAuthentication);
        }

        // For non-API Endpoints the authentication can be done using a Document Cookie
        if (!httpServletRequest.getPathInfo().startsWith("/api")) {

            Optional<String> cookieToken = Arrays.stream(httpServletRequest.getCookies())
                    .filter(x -> x.getName().startsWith("Token="))
                    .map(x -> x.getValue())
                    .findFirst();

            if (cookieToken.isPresent()) {
                UUID token;
                // Try to parse the Header
                try {
                    token = UUID.fromString(cookieToken.get());
                } catch (Exception e) {
                    throw new BadCredentialsException("Malformed Token");
                }

                // If the Token is a valid UUID then pass it onto the AuthenticationFilter as a Credential
                UsernamePasswordAuthenticationToken requestAuthentication = new UsernamePasswordAuthenticationToken(null, token);
                return getAuthenticationManager().authenticate(requestAuthentication);
            }
        }

        throw new BadCredentialsException("No Token was sent with the Request!");
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