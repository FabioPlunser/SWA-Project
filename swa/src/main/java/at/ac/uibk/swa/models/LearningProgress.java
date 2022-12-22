package at.ac.uibk.swa.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.io.Serializable;
import java.util.UUID;

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
    private UUID learningProgressId;

    @Setter
    @Column(name = "learning_interval", nullable = false)
    @JdbcTypeCode(SqlTypes.INTEGER)
    private int interval;

    @Setter
    @Column(name = "e_factor", nullable = false)
    @JdbcTypeCode(SqlTypes.DOUBLE)
    private int eFactor;

    @Setter
    @Column(name = "num_repetitions", nullable = false)
    @JdbcTypeCode(SqlTypes.BIGINT)
    private int repetitions;

    @Setter
    @Column(name = "next_learn", nullable = false)
    @JdbcTypeCode(SqlTypes.TIMESTAMP)
    private int repetitions;
}
