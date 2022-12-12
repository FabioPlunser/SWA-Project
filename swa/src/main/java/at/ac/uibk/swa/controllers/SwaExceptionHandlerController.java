package at.ac.uibk.swa.controllers;

import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class SwaExceptionHandlerController extends ResponseEntityExceptionHandler {

    @ExceptionHandler(Exception.class)
    public String handleException(Exception e) {
        return "500 - Internal Server Error!";
    }

    @ExceptionHandler({ AuthenticationException.class })
    @ResponseBody
    public String handleAuthenticationException(Exception ex) {
        return "AuthenticationError";
    }
}
