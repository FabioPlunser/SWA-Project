package at.ac.uibk.swa.controllers.error_controllers;

import at.ac.uibk.swa.models.rest_responses.MessageResponse;
import at.ac.uibk.swa.models.rest_responses.RestResponseEntity;
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
    public RestResponseEntity handleAuthenticationError(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException accessDeniedException
    ) {
        return MessageResponse.builder()
                .message("Authentication failed!")
                .statusCode(HttpStatus.UNAUTHORIZED)
                .toEntity();
    }

    @ResponseBody
    @RequestMapping(AUTHORIZATION_ERROR_ENDPOINT)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public RestResponseEntity handleAuthorizationError(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException
    ) {
        return MessageResponse.builder()
                .message("Insufficient Privileges!")
                .statusCode(HttpStatus.FORBIDDEN)
                .toEntity();
    }

    @ResponseBody
    @RequestMapping(NOT_FOUND_ERROR_ENDPOINT)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public RestResponseEntity handleNotFoundError(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        return MessageResponse.builder()
                .message("Endpoint not found!")
                .statusCode(HttpStatus.NOT_FOUND)
                .toEntity();
    }

    @ResponseBody
    @RequestMapping(ERROR_ENDPOINT)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public RestResponseEntity handleError(
            HttpServletRequest request,
            HttpServletResponse response,
            Exception exception
    ) {
        return MessageResponse.builder()
                .success(false)
                .message("Internal Server Error!")
                .statusCode(HttpStatus.UNAUTHORIZED)
                .toEntity();
    }

    public void handleErrorManual(
            HttpServletRequest request,
            HttpServletResponse response,
            Exception exception
    ) throws IOException {
        RestResponseEntity responseEntity;

        if (exception instanceof AuthenticationException authenticationException) {
            responseEntity = handleAuthenticationError(request, response, authenticationException);
        } else if (exception instanceof AccessDeniedException accessDeniedException) {
            responseEntity = handleAuthorizationError(request, response, accessDeniedException);
        } else {
            responseEntity = handleError(request, response, exception);
        }

        try {
            response.setStatus(responseEntity.getStatusCode().value());

            String responseBody = SerializationUtil.serializeJSON(responseEntity.getBody());
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