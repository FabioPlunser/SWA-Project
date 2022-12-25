package at.ac.uibk.swa.service.card_service.learning_algorithm;

import at.ac.uibk.swa.models.LearningProgress;

public class LearningAlgorithm {
    public static LearningProgress getUpdatedLearningProgress(LearningProgress current, int difficulty) {
        LearningProgress newLearningProgress = new LearningProgress();
        // TODO: implement algorithm

        // DUMMY:
        newLearningProgress.setRepetitions(current.getRepetitions() + 1);

        return newLearningProgress;
    }
}
