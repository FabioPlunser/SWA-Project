package at.ac.uibk.swa.service.card_service.learning_algorithm;

import at.ac.uibk.swa.models.LearningProgress;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LearningAlgorithm {

    /**
     * This Method computes the next learning date given the current learning progress of a card and the user-set difficulty
     *
     * @param current current learning progress
     * @param difficulty difficulty set by user
     *
     * @return the new LearningProgress object with updated attributes
     */
    public static LearningProgress getUpdatedLearningProgress(
            LearningProgress current,
            int difficulty
    ) {
        // Copy the old LearningProgress
        LearningProgress newLearningProgress = new LearningProgress(current);

        // Increment the number of times this card was learned
        newLearningProgress.incrementRepetitions();

        // Fail: Reset the Interval
        if (difficulty < 3) {
            newLearningProgress.setInterval(1);
        }

        // Answer was right => Adjust interval and eFactor.
        if (difficulty > 2) {
            int n = newLearningProgress.getRepetitions();
            if (n == 1) {
                newLearningProgress.setInterval(1);
            } else if (n == 2) {
                newLearningProgress.setInterval(6);
            } else {
                newLearningProgress.setInterval((int) (current.getInterval() * current.getEFactor()));
                newLearningProgress.setEFactor(Math.max(1.3, current.getEFactor() - 0.8 + 0.28 * difficulty - 0.02 * difficulty * difficulty));
            }
        }

        // Card was not learned enough => Enqueue Card again.
        if (difficulty < 4) {
            // Adding the Card to the Queue again is implemented on the Front-End.
            // But if the user does not learn the Card, ensure that the card's nextLearn Date is due.
            return newLearningProgress;
        }

        // Calculate the time when the card should be learned the next time.
        LocalDateTime nextLearn = LocalDateTime.now().plusDays(newLearningProgress.getInterval());
        newLearningProgress.setNextLearn(nextLearn);

        return newLearningProgress;
    }
}
