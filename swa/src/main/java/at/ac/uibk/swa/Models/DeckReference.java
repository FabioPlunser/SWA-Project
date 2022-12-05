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
    @ToString.Exclude
    @OneToOne(cascade = CascadeType.ALL, optional = false, orphanRemoval = true)
    @JoinColumn(name = "deckId", nullable = false)
    private Deck deck;

    @ManyToOne(optional = false, cascade = CascadeType.ALL)
    @JoinColumn(name = "customerId", nullable = false)
    private Customer customer;

    @Override
    public String toString() {
        return String.format("Deck Reference to : \"%s\" (ID: %s)", this.deck.getName(), this.deck.getDeckId());
    }

    @Override
    public boolean equals(Object obj) {

        if (obj instanceof DeckReference u)
            return u.deck.getDeckId() == this.deck.getDeckId();

        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.deck.getDeckId());
    }
}
