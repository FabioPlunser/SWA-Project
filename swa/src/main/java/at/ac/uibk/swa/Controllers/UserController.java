package at.ac.uibk.swa.Controllers;

import at.ac.uibk.swa.Models.Person;
import at.ac.uibk.swa.Models.RestResponses.CreatedUserResponse;
import at.ac.uibk.swa.Models.RestResponses.MessageResponse;
import at.ac.uibk.swa.Models.RestResponses.RestResponse;
import at.ac.uibk.swa.Service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller responsible for creating and deleting Users.
 */
@RestController
public class UserController {

    @Autowired
    private PersonService personService;


    /**
     * User Registration Endpoint for Users to create an Account by themselves.
     *
     * @param username The new Users username.
     * @param password The new Users password (needs to be already hashed).
     * @param email The new Users email.
     * @return A RestResponse indicating whether the user could be created or not.
     */
    @PostMapping("/api/register")
    public RestResponse register(
            @RequestParam("username") final String username,
            @RequestParam("password") final String password,
            @RequestParam("email") final String email
    ) {
        Person person = new Person(username, email, password, false);
        if (personService.save(person))
            return new CreatedUserResponse(username, person.getCustomerId());
        return new MessageResponse(false, "Could not create User - Username already exists!");
    }

    /**
     * User Creation Endpoint for Admins to manually create Accounts.
     *
     * @param username The new Users username.
     * @param password The new Users password (needs to be already hashed).
     * @param email The new Users email.
     * @param isAdmin Whether the new User should have Admin-Rights.
     * @return A RestResponse indicating whether the user could be created or not.
     */
    @PostMapping("/createUser")
    @PreAuthorize("hasAuthority('ADMIN')")
    public RestResponse createUser(
            @RequestParam("username") final String username,
            @RequestParam("password") final String password,
            @RequestParam("email") final String email,
            @RequestParam("isAdmin") final boolean isAdmin
    ) {
        Person person = new Person(username, email, password, isAdmin);
        if (personService.save(person))
            return new CreatedUserResponse(username, person.getCustomerId());
        return new MessageResponse(false, "Could not create User - Username already exists!");
    }

}
