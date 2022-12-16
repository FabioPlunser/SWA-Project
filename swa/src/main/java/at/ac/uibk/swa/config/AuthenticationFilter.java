package at.ac.uibk.swa.config;

import at.ac.uibk.swa.util.UUIDConversionUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
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
 * @see at.ac.uibk.swa.config.PersonAuthenticationProvider
 * @see AbstractAuthenticationProcessingFilter
 */
@SuppressWarnings("unused")
public class AuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    AuthenticationFilter(final RequestMatcher requiresAuth) {
        super(requiresAuth);
    }

    @SneakyThrows
    @Override
    public Authentication attemptAuthentication(
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse
    ) throws AuthenticationException {
        Optional<UUID> token;

        if (SecurityConfiguration.API_ROUTES.matches(httpServletRequest)) {
            // For API-Endpoints a Bearer Token is required
            token = getAuthHeaderToken(httpServletRequest);
        } else {
            // For non-API Endpoints the authentication can be done using a Document Cookie
            token = getCookieToken(httpServletRequest);
        }

        if (token.isPresent())
        {
            // If the Token is a valid UUID then pass it onto the AuthenticationFilter as a Credential
            UsernamePasswordAuthenticationToken requestAuthentication = new UsernamePasswordAuthenticationToken(null, token);
            return getAuthenticationManager().authenticate(requestAuthentication);
        }

        throw new BadCredentialsException("Test: Username or Password are wrong!");
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

    private static Optional<UUID> getAuthHeaderToken(HttpServletRequest httpServletRequest) {
        // Get the Authorization Header
        Optional<String> authHeader = Optional.ofNullable(httpServletRequest.getHeader(AUTHORIZATION));

        // Try to parse the Header
        try {
            return authHeader
                    .map(header -> header.substring("Bearer".length()).trim())
                    .map(UUID::fromString);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private static Optional<UUID> getCookieToken(HttpServletRequest httpServletRequest) {
        if (httpServletRequest.getCookies() == null)
            return Optional.empty();

        Optional<String> cookieToken = Arrays.stream(httpServletRequest.getCookies())
                .filter(x -> x.getName().equals("Token"))
                .map(Cookie::getValue)
                .findFirst();

        return cookieToken.map(UUIDConversionUtil::tryConvertUUID);
    }
}