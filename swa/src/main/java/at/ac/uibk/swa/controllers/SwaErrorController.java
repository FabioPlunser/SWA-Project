package at.ac.uibk.swa.controllers;

import jakarta.servlet.http.HttpServletRequest;
import lombok.SneakyThrows;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class SwaErrorController implements ErrorController {
    @SneakyThrows
    @RequestMapping("/error")
    public String handleError(HttpServletRequest request) {
        // TODO: Display better error message
        return "<html><head></head><body><h2>404 Page not found</h2></body></html>";
    }
}
