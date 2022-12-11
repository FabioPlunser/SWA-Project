package at.ac.uibk.swa.Controllers;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class SwaExceptionHandlerController {

    @ExceptionHandler(Exception.class)
    public String handleException(Exception e) {
        return "500 - Internal Server Error!";
    }
}
