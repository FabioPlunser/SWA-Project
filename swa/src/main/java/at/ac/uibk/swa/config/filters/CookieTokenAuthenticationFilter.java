package at.ac.uibk.swa.config.filters;

import at.ac.uibk.swa.util.ConversionUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
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
import java.util.Arrays;
import java.util.Optional;

/**
 * Filter for trying to get a Token from the Cookies of a Request.
 *
 * @author David Rieser
 * @see AbstractAuthenticationProcessingFilter
 */
public class CookieTokenAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    public CookieTokenAuthenticationFilter(final RequestMatcher requiresAuth) {
        super(requiresAuth);
    }

    @Override
    public Authentication attemptAuthentication(
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse
    ) throws AuthenticationException {
        Cookie[] cookies = httpServletRequest.getCookies();

        if (cookies != null) {
            Optional<UsernamePasswordAuthenticationToken> authenticationToken =
                    // Iterate over the List of Cookies that were sent with the Request
                    Arrays.stream(cookies)
                            // Get only the Cookies that have a Token
                            .filter(x -> x.getName().equals("Token"))
                            // Get the first Cookie that contains a Token
                            .findFirst()
                            // Get the Value of the Cookie
                            .map(Cookie::getValue)
                            // Try to parse the Cookie as a Token
                            .map(ConversionUtil::tryConvertUUID)
                            // If the Token is a valid UUID then pass it onto the AuthenticationFilter as a Credential
                            .map(token -> new UsernamePasswordAuthenticationToken(null, token));

            // If a Cookie-Token was found, pass it to the AuthenticationManager/AuthenticationProvider.
            if (authenticationToken.isPresent()) {
                return getAuthenticationManager().authenticate(authenticationToken.get());
            }
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
