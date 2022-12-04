package at.ac.uibk.swa.Service;

import at.ac.uibk.swa.Models.Customer;
import at.ac.uibk.swa.Repositories.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

@Service("customerService")
public class CustomerService {

    @Autowired
    CustomerRepository customerRepository;

    public Iterable<Customer> getCustomers() {
        return customerRepository.findAll();
    }

    public Optional<UUID> login(String username, String password) {
        Optional<Customer> optionalUser = customerRepository.findByUsernameAndPasswdHash(username,password);
        if(optionalUser.isPresent()){
            UUID token = UUID.randomUUID();
            Customer customer = optionalUser.get();
            customer.setToken(token);
            // TODO: Token has a unique Constraint => save may fail if the same Token is generated
            //       FIX: Create Loop? Can the Database generate the Token?
            customerRepository.save(customer);
            return Optional.of(token);
        }

        return Optional.empty();
    }

    public Optional<Customer> findByToken(UUID token) {
        // TODO: Should this also get a Username and check if the Token is associated with the given username?
        //       Theoretically not needed because the Token has a unique Constraint
        //       but would make it even harder to brute force for a Token as you would need to guess the username
        //       and the Token at the same time.

        return Optional.of(token)
                .map(customerRepository::findByToken)
                .flatMap(Function.identity());
    }

    public boolean logout(UUID token) {
        Optional<Customer> optionalUser = customerRepository.findByToken(token);
        if(optionalUser.isPresent()){
            Customer customer = optionalUser.get();
            // Delete the Token on Logout
            customer.setToken(null);
            customerRepository.save(customer);
            return true;
        }
        return false;
    }

    public boolean save(Customer customer) {
        try {
            // NOTE: This save may fail if the usernames are equal because username has a unique Constraint
            //       => See Customer.username
            this.customerRepository.save(customer);
            return true;
        } catch (Exception e) {
            throw e;
        }
    }

    public boolean update(Customer customer) {
        return 1 == this.customerRepository.update(
                customer.getUsername(),
                customer.getEmail(),
                customer.getPasswdHash(),
                customer.isAdmin(),
                customer.getCustomerId()
        );
    }
}
