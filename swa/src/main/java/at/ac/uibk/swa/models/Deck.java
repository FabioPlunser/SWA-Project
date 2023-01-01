package at.ac.uibk.swa.models;

import at.ac.uibk.swa.models.annotations.OnlyDeserialize;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "deck")
public class Deck implements Serializable {

    public Deck(String name, String description) {
        this(null, name, description, false, false, false, null, new ArrayList<>(), new ArrayList<>());
    }

    @Id
    @Setter(AccessLevel.PRIVATE)
    @JdbcTypeCode(SqlTypes.NVARCHAR)
    @Column(name = "deck_id", nullable = false)
    @GeneratedValue(strategy=GenerationType.AUTO)
    private UUID deckId;

    @JdbcTypeCode(SqlTypes.NVARCHAR)
    @Column(name = "name", nullable = false)
    private String name;

    @Lob
    // @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "description", nullable = false)
    private String description;

    @JdbcTypeCode(SqlTypes.BOOLEAN)
    @Column(name = "is_published", nullable = false)
    private boolean isPublished;

    @JdbcTypeCode(SqlTypes.BOOLEAN)
    @Column(name = "is_blocked", nullable = false)
    private boolean isBlocked;

    @JdbcTypeCode(SqlTypes.BOOLEAN)
    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted;

    @OnlyDeserialize
    @ManyToOne(optional = false)
    @JoinColumn(name = "creator_id", nullable = false)
    private Person creator;

    @Builder.Default
    @OnlyDeserialize
    @OneToMany(
            mappedBy = "deck",
            orphanRemoval = true,
            fetch=FetchType.EAGER
    )
    private List<Card> cards = new ArrayList<>();

    @OnlyDeserialize
    @Builder.Default
    @ManyToMany(mappedBy = "savedDecks", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Person> subscribedPersons = new ArrayList<>();

    @Override
    public String toString() {
        return String.format("%s - %s", this.name, this.description);
    }

    @Override
    public boolean equals(Object o) {
        return (this == o) || ((o instanceof Deck d) && (this.deckId != null) && (this.deckId.equals(d.deckId)));
    }

    @Override
    public int hashCode() {
        // NOTE: This will intentionally throw an Exception if deckId is null.
        return this.deckId.hashCode();
    }
}
