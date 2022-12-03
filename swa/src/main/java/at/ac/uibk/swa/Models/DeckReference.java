package at.ac.uibk.swa.Models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class DeckReference implements Serializable {

    @Id
    @Column(name = "DeckId", nullable = false)
    @JdbcTypeCode(SqlTypes.UUID)
    private UUID deckId;

    // TODO: Add Creator Reference

    @Override
    public boolean equals(Object obj) {

        if (obj instanceof DeckReference u)
            return u.deckId == this.deckId;

        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.deckId);
    }
}
