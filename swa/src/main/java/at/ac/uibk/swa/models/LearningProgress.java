package at.ac.uibk.swa.models;

import jakarta.persistence.*;
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
    @Column(name = "learning_progress_id", nullable = false)
    @JdbcTypeCode(SqlTypes.NVARCHAR)
    private UUID learningProgressId;

    @Setter
    @Column(name = "interVal", nullable = false)
    @JdbcTypeCode(SqlTypes.INTEGER)
    private int interval;

    @Setter
    @Column(name = "e_factor", nullable = false)
    @JdbcTypeCode(SqlTypes.DOUBLE)
    private int eFactor;

    @Setter
    @Column(name = "repetitions", nullable = false)
    @JdbcTypeCode(SqlTypes.BIGINT)
    private int repetitions;

    //TODO: Explain these many to one relationships
    @ManyToOne
    @JoinColumn(name = "card_id", nullable = false)
    private Card card;

    @ManyToOne
    @JoinColumn(name = "person_id", nullable = false)
    private Person person;
}
