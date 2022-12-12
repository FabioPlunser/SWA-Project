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
@Table(name = "LearningProgress")
public class LearningProgress implements Serializable {

    @Id
    @Column(name = "LearningProcessId", nullable = false)
    @JdbcTypeCode(SqlTypes.INTEGER)
    private UUID learningProcessId;

    @Setter
    @Column(name = "InterVal", nullable = false)
    @JdbcTypeCode(SqlTypes.INTEGER)
    private int interval;

    @Setter
    @Column(name = "EFactor", nullable = false)
    @JdbcTypeCode(SqlTypes.INTEGER)
    private int eFactor;

    @Setter
    @Column(name = "Repetitions", nullable = false)
    @JdbcTypeCode(SqlTypes.INTEGER)
    private int repetitions;
}
