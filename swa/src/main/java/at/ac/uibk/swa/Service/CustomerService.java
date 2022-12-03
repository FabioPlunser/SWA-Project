package at.ac.uibk.swa.Service;

import at.ac.uibk.swa.Models.Customer;
import at.ac.uibk.swa.Repositories.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
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
            customerRepository.save(customer);
            return Optional.of(token);
        }

        return Optional.empty();
    }

    public Optional<User> findByToken(UUID token) {
        Optional<Customer> optionalUser = customerRepository.findByToken(token);
        if(optionalUser.isPresent()){
            Customer customer = optionalUser.get();
            User user = new User(
                    customer.getUsername(), customer.getPasswdHash(),
                    true, true, true, true,
                    AuthorityUtils.createAuthorityList("USER")
            );
            return Optional.of(user);
        }

        return  Optional.empty();
    }

    public void logout(UUID token) {
        Optional<Customer> optionalUser = customerRepository.findByToken(token);
        if(optionalUser.isPresent()){
            Customer customer = optionalUser.get();
            customer.setToken(null);
            customerRepository.save(customer);
        }
    }

    public void save(Customer customer) {
        this.customerRepository.save(customer);
    }
}
