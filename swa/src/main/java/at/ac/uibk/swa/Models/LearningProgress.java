package at.ac.uibk.swa.Models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.io.Serializable;
import java.util.Objects;
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
    @JdbcTypeCode(SqlTypes.NVARCHAR)
    private UUID customerId;

    @Id
    @Column(name = "CardId", nullable = false)
    @JdbcTypeCode(SqlTypes.NVARCHAR)
    private UUID cardId;

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

    @Override
    public boolean equals(Object obj) {

        if (obj instanceof LearningProgressId u)
            return u.cardId == this.cardId && this.customerId == u.customerId;

        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.customerId, this.cardId);
    }

    @NoArgsConstructor
    @AllArgsConstructor
    static class LearningProgressId implements Serializable {
        private UUID customerId;
        private UUID cardId;

        @Override
        public boolean equals(Object obj) {

            if (obj instanceof LearningProgressId u)
                return u.cardId == this.cardId && this.customerId == u.customerId;

            return false;
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.customerId, this.cardId);
        }
    }
}
