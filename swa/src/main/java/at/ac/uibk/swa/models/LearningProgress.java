package at.ac.uibk.swa.models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "learning_progress")
public class LearningProgress implements Serializable, Cloneable {
    @Id
    @Setter(AccessLevel.PACKAGE)
    @Column(name = "progress_id", nullable = false)
    @JdbcTypeCode(SqlTypes.NVARCHAR)
    @GeneratedValue(strategy=GenerationType.AUTO)
    private UUID learningProgressId;

    @Setter
    @Builder.Default
    @Column(name = "learning_interval", nullable = false)
    @JdbcTypeCode(SqlTypes.INTEGER)
    private int interval = 0;

    @Setter
    @Builder.Default
    @Column(name = "e_factor", nullable = false)
    @JdbcTypeCode(SqlTypes.DOUBLE)
    private double eFactor = 2.5;

    @Builder.Default
    @Column(name = "num_repetitions", nullable = false)
    @JdbcTypeCode(SqlTypes.BIGINT)
    private int repetitions = 0;

    @Setter
    @Builder.Default
    @Column(name = "next_learn", nullable = false, columnDefinition = "TIMESTAMP")
    private LocalDateTime nextLearn = LocalDateTime.now();

    public void incrementRepetitions() {
        this.repetitions++;
    }

    public void update(LearningProgress newLearningProgress) {
        this.interval = newLearningProgress.interval;
        this.eFactor = newLearningProgress.eFactor;
        this.repetitions = newLearningProgress.repetitions;
        this.nextLearn = newLearningProgress.nextLearn;
    }

    @Override
    public boolean equals(Object o) {
        return (this == o) || ((o instanceof LearningProgress a) &&
                (this.learningProgressId.equals(a.learningProgressId)));
    }

    @Override
    public int hashCode() {
        return learningProgressId != null ? learningProgressId.hashCode() : 0;
    }

    @SneakyThrows
    public LearningProgress copy() {
        return (LearningProgress) this.clone();
    }

    @Override
    public Object clone()
            throws CloneNotSupportedException
    {
        LearningProgress lp = (LearningProgress) super.clone();

        // NOTE: Using the same reference to the UUID is safe, because UUID is immutable.
        lp.learningProgressId = this.learningProgressId;

        // These are primitive Types and therefore will be implicitly cloned.
        lp.interval = this.interval;
        lp.eFactor = this.eFactor;
        lp.repetitions = this.repetitions;

        // NOTE: Using the same reference to the LocalDateTime is safe, because LocalDateTime is immutable.
        lp.nextLearn = this.nextLearn;

        return lp;
    }
}
