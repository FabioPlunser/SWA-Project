package at.ac.uibk.swa.Models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "Decks")
public class Deck implements Serializable {

    public Deck(
            String name, String description, Person person,
            boolean isPublished, boolean isBlocked, boolean isDeleted
    ) {
        this.deckId = UUID.randomUUID();
        this.name = name;
        this.description = description;
        this.person = person;
        this.isPublished = isPublished;
        this.isBlocked = isBlocked;
        this.isDeleted = isDeleted;
        this.cards = new ArrayList<>();
    }

    public Deck(String name, String description, Person person) {
        this(name, description, person, false, false, false);
    }

    @Id
    // @GeneratedValue(strategy=GenerationType.AUTO)
    @Column(name = "DeckId", nullable = false)
    @JdbcTypeCode(SqlTypes.NVARCHAR)
    private UUID deckId;

    @Setter
    @Column(name = "Name", nullable = false)
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

    @ManyToOne(optional = false)
    @JoinColumn(name = "customerId", nullable = false)
    private Person person;

    @Setter
    @JsonIgnore
    @Builder.Default
    // TODO: Do we need FetchType.EAGER? For the tests using H2 it seems so
    @OneToMany(mappedBy = "deck", cascade = CascadeType.ALL, orphanRemoval = true, fetch=FetchType.EAGER)
    private List<Card> cards = new ArrayList<>();

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
