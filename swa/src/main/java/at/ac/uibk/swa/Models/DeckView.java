package at.ac.uibk.swa.Models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeckView implements Serializable {

    @Id
    @OneToOne(cascade = CascadeType.ALL, optional = false, orphanRemoval = true)
    @JoinColumn(name = "deckId", nullable = false)
    @JdbcTypeCode(SqlTypes.NVARCHAR)
    private Deck deck;

    @Id
    @ManyToOne(optional = false, cascade = CascadeType.ALL)
    @JoinColumn(name = "personId", nullable = false)
    @JdbcTypeCode(SqlTypes.NVARCHAR)
    private Person person;

    @Override
    public String toString() {
        return String.format("Deck View on \"%s\" (ID: %s)", this.deck.getName(), this.deck.getDeckId());
    }

    @Override
    public boolean equals(Object obj) {

        if (obj instanceof DeckView u)
            return u.deck.getDeckId() == this.deck.getDeckId();

        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.deck.getDeckId());
    }
}
