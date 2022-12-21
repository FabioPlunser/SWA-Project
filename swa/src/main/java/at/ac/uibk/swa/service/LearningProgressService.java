package at.ac.uibk.swa.service;

import at.ac.uibk.swa.repositories.LearningProgressRepository;
import org.springframework.stereotype.Service;

@Service("learningProgressService")
public class LearningProgressService {

    LearningProgressRepository learningProgressRepository;

    public LearningProgressService(LearningProgressRepository learningProgressRepository) {
        this.learningProgressRepository = learningProgressRepository;
    }


}
