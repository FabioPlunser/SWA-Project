package at.ac.uibk.swa.repositories;

import at.ac.uibk.swa.models.Deck;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface DeckRepository extends CrudRepository<Deck, UUID> {

}
