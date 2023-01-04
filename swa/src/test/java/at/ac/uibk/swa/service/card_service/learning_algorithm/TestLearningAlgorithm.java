package at.ac.uibk.swa.service.card_service.learning_algorithm;

import at.ac.uibk.swa.models.LearningProgress;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
class TestLearningAlgorithm {
    @ParameterizedTest
    @CsvFileSource(resources = "/learning_algorithm_test_data.csv", numLinesToSkip = 1)
    public void testLearningAlgorithm(int difficulty,
                                      int repetitionBefore,
                                      double efBefore,
                                      int intervalBefore,
                                      int repetitionExpected,
                                      double efExpected,
                                      int intervalExpected) {
        double epsilon = 0.000001d;

        // given: A LearningProgress with parameters from CSV file
        LearningProgress oldLearningProgress = LearningProgress.builder().repetitions(repetitionBefore).eFactor(efBefore).interval(intervalBefore).build();

        // when: updating parameters using a given difficulty
        LearningProgress newLearningProgress = LearningAlgorithm.getUpdatedLearningProgress(oldLearningProgress, difficulty);

        // then: the parameter values should match the expected values
        assertEquals(repetitionExpected, newLearningProgress.getRepetitions());
        assertEquals(efExpected, newLearningProgress.getEFactor(), epsilon);
        assertEquals(intervalExpected, newLearningProgress.getInterval());
    }
}