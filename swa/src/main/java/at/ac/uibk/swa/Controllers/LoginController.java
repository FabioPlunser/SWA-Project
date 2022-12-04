package at.ac.uibk.swa.Controllers;

import at.ac.uibk.swa.Models.Customer;
import at.ac.uibk.swa.Models.RestResponses.*;
import at.ac.uibk.swa.Service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.UUID;

@RestController
public class LoginController {

    @Autowired
    private CustomerService customerService;

    @PostMapping("/api/createUser")
    @PreAuthorize("hasAuthority('ADMIN')")
    public RestResponse createUser(
            @RequestParam("username") final String username,
            @RequestParam("password") final String password,
            @RequestParam("email") final String email,
            @RequestParam("isAdmin") final boolean isAdmin
    ) {
        Customer customer = new Customer(username, email, password, isAdmin);
        if (customerService.save(customer))
            return new CreatedUserResponse(username, customer.getCustomerId());
        return new MessageResponse(false, "Could not create User - Username already exists!");
    }

    @PostMapping({"/api/login", "/token"})
    public RestResponse getToken(@RequestParam("username") final String username, @RequestParam("password") final String password) {
        Optional<UUID> token = customerService.login(username, password);

        if(token.isEmpty())
            return new AuthFailedResponse("Username or Password are wrong!");

        return new TokenResponse(token.get());
    }
}