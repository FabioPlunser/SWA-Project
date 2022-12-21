package at.ac.uibk.swa.service;

import at.ac.uibk.swa.repositories.LearningProgressRepository;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service("learningProgressService")
public class LearningProgressService {

    LearningProgressRepository learningProgressRepository;

    @Getter
    private Integer learningInterval;
    @Getter
    private Double eFactor;
    @Getter
    private Integer repetitions;
    @Getter
    private LocalDateTime nextLearningDate;

    public LearningProgressService(LearningProgressRepository learningProgressRepository) {
        this.learningProgressRepository = learningProgressRepository;
        this.learningInterval = 0;
        this.eFactor = 2.5;
        this.repetitions = 0;
        this.nextLearningDate = LocalDateTime.now();
    }

    //difficulty is a parameter set by user after attempting to learn a card
    public void determineNextLearningDate(int difficulty) {
        repetitions += 1;

        if (difficulty < 3) {
            learningInterval = 1;
        }
        if (difficulty < 4) {
            nextLearningDate = LocalDateTime.now();
        }
        if (difficulty > 2) {
            //TODO: Exception handling in case repetitions is negative?
            if (repetitions == 1) {
                learningInterval = 1;
            } else if (repetitions == 2) {
                learningInterval = 6;
            } else {
                learningInterval = (int) (learningInterval * eFactor);
                eFactor = Math.max(1.3, eFactor - 0.8 + 0.28*difficulty - 0.02*difficulty*difficulty);
            }
        }
    }
}
