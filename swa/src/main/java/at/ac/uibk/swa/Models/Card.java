package at.ac.uibk.swa.Models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "Cards")
public class Card implements Serializable {

    @Id
    @Column(name = "CardId", nullable = false)
    @JdbcTypeCode(SqlTypes.UUID)
    private UUID cardId;

    @Column(name = "FrontText", nullable = false)
    @JdbcTypeCode(SqlTypes.NVARCHAR)
    private String frontText;

    @Column(name = "BackText", nullable = false)
    @JdbcTypeCode(SqlTypes.NVARCHAR)
    private String backText;

    @Column(name = "IsFlipped", nullable = false)
    @JdbcTypeCode(SqlTypes.BOOLEAN)
    private boolean isFlipped;

    @Override
    public String toString() {
        return String.format("Card: %s - %s", this.frontText, this.backText);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Card u)
            return u.cardId == this.cardId;

        return false;
    }

    @Override
    public int hashCode() {
        return this.cardId.hashCode();
    }
}
