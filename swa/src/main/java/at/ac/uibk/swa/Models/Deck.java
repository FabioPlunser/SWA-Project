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
@Table(name = "Decks")
public class Deck implements Serializable {

    @Id
    @Column(name = "DeckId", nullable = false)
    @JdbcTypeCode(SqlTypes.UUID)
    private UUID deckId;

    @Column(name = "Name", nullable = false)
    @JdbcTypeCode(SqlTypes.NVARCHAR)
    private String name;

    @Column(name = "Description", nullable = false)
    @JdbcTypeCode(SqlTypes.NVARCHAR)
    private String description;

    @Column(name = "IsPublished", nullable = false)
    @JdbcTypeCode(SqlTypes.BOOLEAN)
    private boolean isPublished;

    @Column(name = "IsBlocked", nullable = false)
    @JdbcTypeCode(SqlTypes.BOOLEAN)
    private boolean isBlocked;

    @Column(name = "IsDeleted", nullable = false)
    @JdbcTypeCode(SqlTypes.BOOLEAN)
    private boolean isDeleted;

    // TODO: Add Field for Creator

    @Override
    public String toString() {
        return String.format("%s - %s", name, description);
    }

    @Override
    public boolean equals(Object obj) {

        if (obj instanceof Deck u)
            return u.deckId == this.deckId;

        return false;
    }

    @Override
    public int hashCode() {
        return this.deckId.hashCode();
    }
}
