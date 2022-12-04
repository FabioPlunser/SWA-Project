package at.ac.uibk.swa.Repositories;

import at.ac.uibk.swa.Models.Deck;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface DeckRepository extends CrudRepository<Deck, UUID> {

}
