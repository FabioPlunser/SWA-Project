package at.ac.uibk.swa.Repositories;

import at.ac.uibk.swa.Models.Customer;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

public interface CustomerRepository extends CrudRepository<Customer, UUID> {
    @Override
    Iterable<Customer> findAll();

    Optional<Customer> findByToken(UUID token);

    Optional<Customer> findByUsernameAndPasswdHash(String username, String passwdHash);

    @Transactional
    @Modifying
    @Query("""
            update Customer c 
            set c.username = ?1, c.email = ?2, c.passwdHash = ?3, c.isAdmin = ?4
            where c.customerId = ?5""")
    int update(String username, String email, String passwdHash, boolean isAdmin, UUID customerId);


}