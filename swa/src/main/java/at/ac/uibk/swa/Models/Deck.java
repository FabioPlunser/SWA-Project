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

    @Setter
    @Column(name = "Name", nullable = false, unique = true)
    @JdbcTypeCode(SqlTypes.NVARCHAR)
    private String name;

    @Setter
    @Column(name = "Description", nullable = false)
    @JdbcTypeCode(SqlTypes.NVARCHAR)
    private String description;

    @Setter
    @Column(name = "IsPublished", nullable = false)
    @JdbcTypeCode(SqlTypes.BOOLEAN)
    private boolean isPublished;

    @Setter
    @Column(name = "IsBlocked", nullable = false)
    @JdbcTypeCode(SqlTypes.BOOLEAN)
    private boolean isBlocked;

    @Setter
    @Column(name = "IsDeleted", nullable = false)
    @JdbcTypeCode(SqlTypes.BOOLEAN)
    private boolean isDeleted;

    // TODO: Create User and Card Reference

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
