import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class LearningProgress {
    private int numRepetitions;
    private double eFactor;
    private int learningInterval;
    private LocalDateTime nextLearningDate;

    public LearningProgress() {
        numRepetitions = 0;
        eFactor = 2.5;
        learningInterval = 0;
        nextLearningDate = LocalDateTime.now();
    }

    public void giveFeedback(int difficulty) {
        numRepetitions += 1;

        if (difficulty < 3) {
            learningInterval = 1;
        }

        if (difficulty < 4) {
            nextLearningDate = LocalDateTime.now();
        }

        if (difficulty > 2) {
            if (numRepetitions == 1) {
                learningInterval = 1;
            } else if (numRepetitions == 2) {
                learningInterval = 6;
            } else {
                learningInterval = (int)(learningInterval * eFactor);
                eFactor = Math.max(1.3, eFactor - 0.8 + 0.28 * difficulty - 0.02 * difficulty * difficulty);
            }
            nextLearningDate = nextLearningDate.truncatedTo(ChronoUnit.DAYS).plusDays(learningInterval);
        }
    }

    public LocalDateTime getNextLearningDate() {
        return nextLearningDate;
    }
}
