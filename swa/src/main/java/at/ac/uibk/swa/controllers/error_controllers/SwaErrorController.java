package at.ac.uibk.swa.controllers.error_controllers;

import at.ac.uibk.swa.models.rest_responses.MessageResponse;
import at.ac.uibk.swa.models.rest_responses.RestResponse;
import at.ac.uibk.swa.util.SerializationUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.IOException;
import java.io.PrintWriter;

import static at.ac.uibk.swa.util.EndpointMatcherUtil.ErrorEndpoints.*;

/**
 * Controller for sending Error Responses.
 *
 * @author David Rieser
 */
@Controller
@SuppressWarnings("unused")
public class SwaErrorController implements ErrorController {

    @ResponseBody
    @RequestMapping(AUTHENTICATION_ERROR_ENDPOINT)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public RestResponse handleAuthenticationError(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException accessDeniedException
    ) {
        return new MessageResponse(false, "Authentication failed!");
    }

    @ResponseBody
    @RequestMapping(AUTHORIZATION_ERROR_ENDPOINT)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public RestResponse handleAuthorizationError(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException
    ) {
        return new MessageResponse(false, "Insufficient Privileges!");
    }

    @ResponseBody
    @RequestMapping(NOT_FOUND_ERROR_ENDPOINT)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public RestResponse handleNotFoundError(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        return new MessageResponse(false, "Endpoint not found!");
    }

    @ResponseBody
    @RequestMapping(ERROR_ENDPOINT)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public RestResponse handleError(
            HttpServletRequest request,
            HttpServletResponse response,
            Exception exception
    ) {
        return new MessageResponse(false, "Internal Server Error!");
    }

    public void handleErrorManual(
            HttpServletRequest request,
            HttpServletResponse response,
            Exception exception
    ) throws IOException {
        RestResponse restResponse;

        if (exception instanceof AuthenticationException authenticationException) {
            restResponse = handleAuthenticationError(request, response, authenticationException);
        } else if (exception instanceof AccessDeniedException accessDeniedException) {
            restResponse = handleAuthorizationError(request, response, accessDeniedException);
        } else {
            restResponse = handleError(request, response, exception);
        }

        try {
            response.setStatus(restResponse.getStatusCode());

            String responseBody = SerializationUtil.serializeJSON(restResponse);
            if (responseBody != null) {
                try (PrintWriter writer = response.getWriter()) {
                    writer.write(responseBody);
                }
            }
        } catch (JsonProcessingException e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
