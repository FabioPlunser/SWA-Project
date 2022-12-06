package at.ac.uibk.swa.Controllers;

import at.ac.uibk.swa.Models.RestResponses.MessageResponse;
import at.ac.uibk.swa.Models.RestResponses.RestResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class SwaErrorController implements ErrorController {
    @RequestMapping("/error")
    @ResponseBody
    public RestResponse handleError(HttpServletRequest request) {
        // TODO: This is hardly human readable if they get redirected to a invalid Endpoint
        //       => There has to be a way to check which stage failed (Auth or Endpoint or Exception)
        return new MessageResponse(false, "Could not find Endpoint!");
        /*
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        Exception exception = (Exception) request.getAttribute("javax.servlet.error.exception");
        return String.format("<html><body><h2>Error Page</h2><div>Status code: <b>%s</b></div>"
                        + "<div>Exception Message: <b>%s</b></div><body></html>",
                statusCode, exception==null? "N/A": exception.getMessage());
         */
    }
}
