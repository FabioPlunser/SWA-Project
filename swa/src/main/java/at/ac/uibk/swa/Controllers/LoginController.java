package at.ac.uibk.swa.Controllers;

import at.ac.uibk.swa.Config.AuthContext;
import at.ac.uibk.swa.Models.RestResponses.AuthFailedResponse;
import at.ac.uibk.swa.Models.RestResponses.MessageResponse;
import at.ac.uibk.swa.Models.RestResponses.RestResponse;
import at.ac.uibk.swa.Models.RestResponses.TokenResponse;
import at.ac.uibk.swa.Service.PersonService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.UUID;

/**
 * Controller responsible for creating and deleting Authentication Tokens.
 */
@RestController
public class LoginController {

    @Autowired
    private PersonService personService;

    /**
     * Endpoint for the Front-End to request an Authentication Token.
     *
     * @param username The username of the User to create the Token for.
     * @param password The password of the User to create the Token for.
     * @return A Token if the user credentials are correct, otherwise an error.
     */
    @PostMapping({"/api/login", "/token"})
    public RestResponse getToken(
            HttpServletResponse response,
            @RequestParam("username") final String username,
            @RequestParam("password") final String password
    ) {
        Optional<UUID> maybeToken = personService.login(username, password);

        if(maybeToken.isEmpty())
            return new AuthFailedResponse("Username or Password are wrong!");

        UUID token = maybeToken.get();

        // https://www.baeldung.com/spring-response-header#1-usinghttpservletresponse
        // https://developer.mozilla.org/en-US/docs/Web/HTTP/Cookies
        response.setHeader("Set-Cookie",
                String.format("Token=%s;Max-Age=3600", token.toString()));

        return new TokenResponse(token);
    }

    /**
     * Endpoint for the Front-End to logout.
     * This deletes the Authentication Token stored in the database.
     *
     * @return A Message saying whether the Logout was successful or not.
     */
    @PostMapping("/api/logout")
    public RestResponse deleteToken() {
        Optional<UUID> token = AuthContext.getLoginToken();

        if (token.isEmpty() || !personService.logout(token.get()))
            return new MessageResponse(false, "No matching Token!");

        return new MessageResponse(true, "Successfully logged out!");
    }
}