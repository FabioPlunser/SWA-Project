package at.ac.uibk.swa.controllers;

import at.ac.uibk.swa.config.AuthContext;
import at.ac.uibk.swa.models.RestResponses.AuthFailedResponse;
import at.ac.uibk.swa.models.RestResponses.MessageResponse;
import at.ac.uibk.swa.models.RestResponses.RestResponse;
import at.ac.uibk.swa.models.RestResponses.TokenResponse;
import at.ac.uibk.swa.service.PersonService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
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
    @SneakyThrows
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