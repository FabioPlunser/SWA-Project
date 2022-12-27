package at.ac.uibk.swa.models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "learning_progress")
public class LearningProgress implements Serializable {
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

    @Setter
    @Builder.Default
    @Column(name = "num_repetitions", nullable = false)
    @JdbcTypeCode(SqlTypes.BIGINT)
    private int repetitions = 0;

    @Setter
    @Builder.Default
    @Column(name = "next_learn", nullable = false)
    @JdbcTypeCode(SqlTypes.DATE)
    @Temporal(TemporalType.DATE)
    private Date nextLearn = new Date();

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
}
