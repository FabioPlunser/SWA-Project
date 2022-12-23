package at.ac.uibk.swa.repositories;

import at.ac.uibk.swa.models.Deck;
import at.ac.uibk.swa.models.Person;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

public interface DeckRepository extends CrudRepository<Deck, UUID> {
    @Override
    List<Deck> findAll();
}
