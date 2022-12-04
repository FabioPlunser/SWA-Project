package at.ac.uibk.swa.Service;

import at.ac.uibk.swa.Models.Customer;
import at.ac.uibk.swa.Repositories.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

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
        Optional<Customer> optionalUser = customerRepository.findByToken(token);
        if(optionalUser.isPresent()){
            Customer customer = optionalUser.get();
            return Optional.of(customer);
        }

        return  Optional.empty();
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
            // NOTE: This save may fail if the username's are equal because username has a unique Constraint
            //       => See Customer.username
            this.customerRepository.save(customer);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
