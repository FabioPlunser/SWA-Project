package at.ac.uibk.swa.controllers.errorControllers;

import at.ac.uibk.swa.util.EndpointMatcherUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.naming.AuthenticationException;
import java.io.IOException;

import static at.ac.uibk.swa.util.EndpointMatcherUtil.authenticationErrorEndpoint;
import static at.ac.uibk.swa.util.EndpointMatcherUtil.authorizationErrorEndpoint;


@ControllerAdvice
@SuppressWarnings("unused")
public class SwaExceptionHandlerController extends ResponseEntityExceptionHandler {

    // https://developer.mozilla.org/en-US/docs/Web/HTTP/Status#client_error_responses

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(AuthenticationException.class)
    public void handleAuthenticationException(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        if (EndpointMatcherUtil.isApiRoute(request)) {
            response.sendRedirect(authenticationErrorEndpoint);
        } else {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Forbidden!");
        }
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(AccessDeniedException.class)
    public void handleAccessDeniedException(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        response.sendRedirect(authorizationErrorEndpoint);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public String handleException(Exception e) {
        return "Internal Server Error!";
    }
}
