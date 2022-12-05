package at.ac.uibk.swa.Repositories;

import at.ac.uibk.swa.Models.Person;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

public interface PersonRepository extends CrudRepository<Person, UUID> {
    @Override
    Iterable<Person> findAll();

    Optional<Person> findByToken(UUID token);

    Optional<Person> findByUsernameAndPasswdHash(String username, String passwdHash);

    @Transactional
    @Modifying
    @Query("""
            update Customer c 
            set c.username = ?1, c.email = ?2, c.passwdHash = ?3, c.isAdmin = ?4
            where c.customerId = ?5""")
    int update(String username, String email, String passwdHash, boolean isAdmin, UUID customerId);


}