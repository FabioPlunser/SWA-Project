package at.ac.uibk.swa.service.card_service.learning_algorithm;

import at.ac.uibk.swa.models.LearningProgress;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class LearningAlgorithm {
    public static LearningProgress getUpdatedLearningProgress(LearningProgress current, int difficulty) {
        LearningProgress newLearningProgress = new LearningProgress();
        newLearningProgress.setRepetitions(current.getRepetitions() + 1);

        if (difficulty < 3) {
            newLearningProgress.setInterval(1);
        }
        if (difficulty < 4) {
            //TODO warum nicht LocalDate statt Date?
            newLearningProgress.setNextLearn(Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()));
        }
        if (difficulty > 2) {
            if (current.getRepetitions() == 1) {
                newLearningProgress.setInterval(1);
            } else if (current.getRepetitions() == 2) {
                newLearningProgress.setInterval(6);
            } else {
                newLearningProgress.setInterval((int) (current.getInterval() * current.getEFactor()));
                newLearningProgress.setEFactor(Math.max(1.3, current.getEFactor() - 0.8 + 0.28*difficulty - 0.02*difficulty*difficulty));
            }
        }

        return newLearningProgress;
    }
}
