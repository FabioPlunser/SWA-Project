package at.ac.uibk.swa.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandlerImpl;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static at.ac.uibk.swa.util.EndpointMatcherUtil.authorizationErrorEndpoint;

@Component
public class RestAccessDeniedHandler extends AccessDeniedHandlerImpl {

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException
    ) throws IOException, ServletException {
        response.sendRedirect(authorizationErrorEndpoint);
    }

    @Override
    public void setErrorPage(String errorPage) {
        super.setErrorPage(errorPage);
    }
}
