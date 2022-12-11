package at.ac.uibk.swa.Controllers;

import at.ac.uibk.swa.Models.Annotations.Admin;
import at.ac.uibk.swa.Models.Permission;
import at.ac.uibk.swa.Models.Person;
import at.ac.uibk.swa.Models.RestResponses.CreatedUserResponse;
import at.ac.uibk.swa.Models.RestResponses.ListResponse;
import at.ac.uibk.swa.Models.RestResponses.MessageResponse;
import at.ac.uibk.swa.Models.RestResponses.RestResponse;
import at.ac.uibk.swa.Service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

/**
 * Controller responsible for creating and deleting Users.
 */
@RestController
public class PersonController {

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
        UUID token = UUID.randomUUID();
        Person person = new Person(username, email, password, token, Permission.defaultPermissions());

        return saveUser(person);
    }

    /**
     * User Creation Endpoint for Admins to manually create Accounts.
     *
     * @param username The new Users username.
     * @param password The new Users password (needs to be already hashed).
     * @param email The new Users email.
     * @param permissions The Permissions the new User should have.
     * @return A RestResponse indicating whether the user could be created or not.
     */
    @Admin
    @PostMapping("/api/createUser")
    public RestResponse create(
            @RequestParam("username") final String username,
            @RequestParam("password") final String password,
            @RequestParam("email") final String email,
            @RequestParam("permissions") final List<Permission> permissions
    ) {
        Person person = new Person(username, email, password, permissions);

        return saveUser(person);
    }

    /**
     * Helper Function for saving a Person and returning a corresponding RestResponse.
     *
     * @param person The Person to save.
     * @return A RestResponse indicating whether the operation was successful or not.
     */
    private RestResponse saveUser(Person person) {
        if (!personService.save(person))
            return new MessageResponse(false, "Could not create User - Username already exists!");

        return new CreatedUserResponse(person.getUsername(), person.getPersonId());
    }

    /**
     * Endpoint for Admins to get all users.
     *
     * @return A RestReponse containing a List of all users.
     */
    @Admin
    @GetMapping("/api/getAllUsers")
    public RestResponse getAllUsers() {
        return new ListResponse<Person>(personService.getPersons());
    }

    /**
     * Endpoint for Admins to delete a user.
     *
     * @param personId The ID of the User to delete
     * @return A RestResponse indicating whether the operation was successful or not.
     */
    @Admin
    @PostMapping("/api/deleteUser")
    public RestResponse deleteUser(
            @RequestParam("personId") final UUID personId
    ) {
        if (!personService.delete(personId))
            return new MessageResponse(false, "Could not delete User - User does not exist!");

        return new MessageResponse(true, "User deleted successfully!");
    }

    /**
     * Endpoint for Admins to change/update a user
     *
     * @param personId The ID of the User to update
     * @param username
     * @param email
     * @param password
     * @param permissions
     * @return success message
     */
    @Admin
    @PostMapping("/api/updateUser")
    public RestResponse updateUser(
            @RequestParam("personId") final UUID personId,
            @RequestParam(name = "username", required = false) final String username,
            @RequestParam(name = "email", required = false) final String email,
            @RequestParam(name = "permissions", required = false) final List<Permission> permissions,
            @RequestParam(name = "password", required = false) final String password
    ) {
        if (personService.update(personId, username, email, permissions, password))
            return new MessageResponse(true, "User updated successfully!");

        return new MessageResponse(false, "Could not update User - User does not exist!");
    }

    /**
     * Endpoint for Admins to get all possible Permission so that they don't need to be changed manually on frontend.
     *
     * @return A List of all possible Permissions.
     */
    @Admin
    @GetMapping("/api/getAllPermissions")
    public RestResponse getAllPermissions() {
        return new ListResponse<>(Stream.of(Permission.values()).map(Enum::name).toList());
    }
}
