package at.ac.uibk.swa.repositories;

import at.ac.uibk.swa.models.LearningProgress;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface LearningProgressRepository  extends CrudRepository<LearningProgress, UUID> {
}
