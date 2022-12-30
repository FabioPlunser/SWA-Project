package at.ac.uibk.swa.config.exception_handling;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static at.ac.uibk.swa.util.EndpointMatcherUtil.ErrorEndpoints.AUTHENTICATION_ERROR_ENDPOINT;

/**
 * The Authentication Entry Point of the Spring Security Chain.
 * <br/>
 * This is used for catching {@link AuthenticationException}s.
 *
 * @author David Rieser
 * @see AuthenticationException
 * @see AuthenticationEntryPoint
 */
@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(
            HttpServletRequest request, HttpServletResponse response, AuthenticationException authException
    ) throws IOException {
        response.sendRedirect(AUTHENTICATION_ERROR_ENDPOINT);
    }
}
