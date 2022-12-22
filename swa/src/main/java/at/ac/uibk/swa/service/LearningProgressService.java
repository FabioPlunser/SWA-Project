package at.ac.uibk.swa.service;

import at.ac.uibk.swa.models.Card;
import at.ac.uibk.swa.models.LearningProgress;
import at.ac.uibk.swa.models.Person;
import at.ac.uibk.swa.repositories.LearningProgressRepository;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service("learningProgressService")
public class LearningProgressService {

    LearningProgressRepository learningProgressRepository;

    @Getter
    private int learningInterval;
    @Getter
    private double eFactor;
    @Getter
    private int repetitions;
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
    public void determineNextLearningDate(int difficulty, Person person, Card card) {
        repetitions += 1;

        if (difficulty < 3) {
            learningInterval = 1;
            //TODO: ??
            learningProgressRepository.save(new LearningProgress(UUID.randomUUID(), learningInterval, eFactor, repetitions, card, person));
        }
        if (difficulty < 4) {
            nextLearningDate = LocalDateTime.now();
        }
        if (difficulty > 2) {
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
