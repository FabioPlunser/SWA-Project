package at.ac.uibk.swa.Controllers;

import at.ac.uibk.swa.Models.Exceptions.TokenExpiredException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException exc
    ) throws IOException, ServletException {
        if (exc instanceof TokenExpiredException) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token expired");
        }
        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal Error");
    }
}