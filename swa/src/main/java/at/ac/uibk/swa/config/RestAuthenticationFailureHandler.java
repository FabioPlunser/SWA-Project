package at.ac.uibk.swa.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static at.ac.uibk.swa.util.EndpointMatcherUtil.authenticationErrorEndpoint;

@Component
public class RestAuthenticationFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(
            HttpServletRequest request, HttpServletResponse response, AuthenticationException authException
    ) throws IOException {
        response.sendRedirect(authenticationErrorEndpoint);
    }
}
