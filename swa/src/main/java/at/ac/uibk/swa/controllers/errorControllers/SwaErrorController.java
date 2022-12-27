package at.ac.uibk.swa.controllers.errorControllers;

import at.ac.uibk.swa.models.restResponses.MessageResponse;
import at.ac.uibk.swa.models.restResponses.RestResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

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
    @RequestMapping(authenticationErrorEndpoint)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public RestResponse handleAuthenticationError(HttpServletRequest request) {
        return new MessageResponse(false, "Authentication failed!");
    }

    @ResponseBody
    @RequestMapping(authorizationErrorEndpoint)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public RestResponse handleAuthorizationError(HttpServletRequest request) {
        return new MessageResponse(false, "Insufficient Privileges!");
    }

    @ResponseBody
    @RequestMapping(notFoundErrorEndpoint)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public RestResponse handleNotFoundError(HttpServletRequest request) {
        return new MessageResponse(false, "Endpoint not found!");
    }

    @ResponseBody
    @RequestMapping(errorEndpoint)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public RestResponse handleError(HttpServletRequest request) {
        return new MessageResponse(false, "Internal Server Error!");
    }
}
