package at.ac.uibk.swa.models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.io.Serializable;
import java.util.UUID;
import java.util.Date;
import java.util.function.Function;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "learning_progress")
public class LearningProgress implements Serializable {
    @Id
    @Column(name = "progress_id", nullable = false)
    @JdbcTypeCode(SqlTypes.NVARCHAR)
    @GeneratedValue(strategy=GenerationType.AUTO)
    private UUID learningProgressId;

    @Setter
    @Column(name = "learning_interval", nullable = false)
    @JdbcTypeCode(SqlTypes.INTEGER)
    private int interval = 0;

    @Setter
    @Column(name = "e_factor", nullable = false)
    @JdbcTypeCode(SqlTypes.DOUBLE)
    private double eFactor = 2.5;

    @Setter
    @Column(name = "num_repetitions", nullable = false)
    @JdbcTypeCode(SqlTypes.BIGINT)
    private int repetitions = 0;

    @Setter
    @Column(name = "next_learn", nullable = false)
    @JdbcTypeCode(SqlTypes.TIMESTAMP)
    private Date nextLearn = new Date();

    public void update(LearningProgress newLearningProgress) {
        this.interval = newLearningProgress.interval;
        this.eFactor = newLearningProgress.eFactor;
        this.repetitions = newLearningProgress.repetitions;
        this.nextLearn = newLearningProgress.nextLearn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LearningProgress that = (LearningProgress) o;

        return learningProgressId != null ? learningProgressId.equals(that.learningProgressId) : that.learningProgressId == null;
    }

    @Override
    public int hashCode() {
        return learningProgressId != null ? learningProgressId.hashCode() : 0;
    }
}
