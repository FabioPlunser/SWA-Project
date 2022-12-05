package at.ac.uibk.swa.Controllers;

import at.ac.uibk.swa.Models.RestResponses.*;
import at.ac.uibk.swa.Service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
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
    @PostMapping({"/api/login", "/token", "/login"})
    public RestResponse getToken(
            @RequestParam("username") final String username,
            @RequestParam("password") final String password
    ) {
        Optional<UUID> token = personService.login(username, password);

        if(token.isEmpty())
            return new AuthFailedResponse("Username or Password are wrong!");

        return new TokenResponse(token.get());
    }

    /**
     * Endpoint for the Front-End to logout.
     * This deletes the Authentication Token stored in the database.
     *
     * @return A Token if the user credentials are correct, otherwise an error.
     */
    @PostMapping("/api/logout")
    public RestResponse deleteToken() {
        UUID token = (UUID) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!personService.logout(token))
            return new MessageResponse(false, "No matching Token!");

        return new MessageResponse(true, "Successfully logged out!");
    }
}