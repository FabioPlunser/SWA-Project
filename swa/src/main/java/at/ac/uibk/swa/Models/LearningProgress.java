package at.ac.uibk.swa.Models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.io.Serializable;
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@Table(name = "LearningProgress")
@IdClass(LearningProgress.LearningProgressId.class)
public class LearningProgress {

    @Id
    @Column(name = "CustomerId", nullable = false)
    @JdbcTypeCode(SqlTypes.UUID)
    private UUID customerId;

    @Id
    @Column(name = "CardId", nullable = false)
    @JdbcTypeCode(SqlTypes.UUID)
    private UUID cardId;

    @NoArgsConstructor
    @AllArgsConstructor
    static class LearningProgressId implements Serializable {
        private UUID customerId;
        private UUID cardId;
    }
}
