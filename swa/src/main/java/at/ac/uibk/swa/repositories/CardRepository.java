package at.ac.uibk.swa.repositories;

import at.ac.uibk.swa.models.Card;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

public interface CardRepository extends CrudRepository<Card, UUID> {
    @Override
    List<Card> findAll();
}
