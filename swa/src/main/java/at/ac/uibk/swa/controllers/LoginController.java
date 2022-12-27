package at.ac.uibk.swa.controllers;

import at.ac.uibk.swa.config.personAuthentication.AuthContext;
import at.ac.uibk.swa.models.Person;
import at.ac.uibk.swa.models.restResponses.AuthFailedResponse;
import at.ac.uibk.swa.models.restResponses.LoginResponse;
import at.ac.uibk.swa.models.restResponses.MessageResponse;
import at.ac.uibk.swa.models.restResponses.RestResponse;
import at.ac.uibk.swa.service.PersonService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.UUID;

import static at.ac.uibk.swa.util.EndpointMatcherUtil.loginEndpoint;
import static at.ac.uibk.swa.util.EndpointMatcherUtil.logoutEndpoint;

/**
 * Controller handling the login-, and logout-process.
 *
 * @author David Rieser
 * @see at.ac.uibk.swa.util.EndpointMatcherUtil
 */
@SuppressWarnings("unused")
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
    @PostMapping(loginEndpoint)
    public RestResponse getToken(
            HttpServletResponse response,
            @RequestParam("username") final String username,
            @RequestParam("password") final String password
    ) {
        Optional<Person> maybePerson = personService.login(username, password);

        if(maybePerson.isEmpty())
            return new AuthFailedResponse("Username or Password are wrong!");

        return new LoginResponse(maybePerson.get());
    }

    /**
     * Endpoint for the Front-End to logout.
     * This deletes the Authentication Token stored in the database.
     *
     * @return A Message saying whether the Logout was successful or not.
     */
    @PostMapping(logoutEndpoint)
    public RestResponse deleteToken() {
        Optional<UUID> token = AuthContext.getLoginToken();

        if (token.isEmpty() || !personService.logout(token.get()))
            return new MessageResponse(false, "No matching Token!");

        return new MessageResponse(true, "Successfully logged out!");
    }
}