package at.ac.uibk.swa.service;

import at.ac.uibk.swa.repositories.LearningProgressRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("learningProgressService")
public class LearningProgressService {

    LearningProgressRepository learningProgressRepository;

    Integer learningInterval;
    Double eFactor;
    Integer repetitions;

    public LearningProgressService(LearningProgressRepository learningProgressRepository) {
        this.learningProgressRepository = learningProgressRepository;
    }
}
