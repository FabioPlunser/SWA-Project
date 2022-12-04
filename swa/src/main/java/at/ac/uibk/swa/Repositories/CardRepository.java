package at.ac.uibk.swa.Repositories;

import at.ac.uibk.swa.Models.Card;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface CardRepository extends CrudRepository<Card, UUID> {
}
