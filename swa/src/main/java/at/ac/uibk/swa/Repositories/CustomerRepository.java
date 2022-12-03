package at.ac.uibk.swa.Repositories;

import at.ac.uibk.swa.Models.Customer;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface CustomerRepository extends CrudRepository<Customer, UUID> {
    @Override
    Iterable<Customer> findAll();

    Optional<Customer> findByToken(UUID token);

    Optional<Customer> findByUsernameAndPasswdHash(String username, String passwdHash);

}