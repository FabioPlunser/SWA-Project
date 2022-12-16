package at.ac.uibk.swa.repositories;

import at.ac.uibk.swa.models.Person;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PersonRepository extends CrudRepository<Person, UUID> {
    @Override
    List<Person> findAll();

    Optional<Person> findByToken(UUID token);

    Optional<Person> findByUsername(String username);
}