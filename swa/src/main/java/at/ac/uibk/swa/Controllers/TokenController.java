package at.ac.uibk.swa.Controllers;

import at.ac.uibk.swa.Models.Customer;
import at.ac.uibk.swa.Service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
public class TokenController {

    @Autowired
    private CustomerService customerService;

    @GetMapping("/api/createUser")
    public String createUser(@RequestBody String username, @RequestBody String password) {
        // TODO: Send Email and check that requesting User is admin
        customerService.save(new Customer(
                UUID.randomUUID(), username, username + "@gmail.com",
                password, true, UUID.randomUUID())
        );
        // TODO: Create MessageWrapper that can be converted into JSON
        return "{\"message\": \"Created User\"}";
    }

    @GetMapping("/api/token")
    //@GetMapping("/api/login")
    public String getToken(@RequestBody String username, @RequestBody String password) {
        Optional<UUID> token = customerService.login(username, password);

        // TODO: Create AuthenticationException and TokenWrapper that can be converted into JSON
        if(token.isEmpty()) return "{\"error\": \"No token found\"}";

        return "{\"token\": \"" + token.toString() + "\"}";
    }
}