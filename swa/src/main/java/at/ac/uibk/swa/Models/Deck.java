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
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "Decks")
public class Deck implements Serializable {

    public Deck(String name, String description, Person creator) {
        this(
                null, name, description,
                false, false, false,
                creator, new ArrayList<>(), new ArrayList<>()
        );
    }

    @Id
    @JdbcTypeCode(SqlTypes.NVARCHAR)
    @Column(name = "DeckId", nullable = false)
    @GeneratedValue(strategy=GenerationType.AUTO)
    private UUID deckId;

    @Setter
    @JdbcTypeCode(SqlTypes.NVARCHAR)
    @Column(name = "Name", nullable = false)
    private String name;

    @Setter
    @JdbcTypeCode(SqlTypes.NVARCHAR)
    @Column(name = "Description", nullable = false)
    private String description;

    @Setter
    @JdbcTypeCode(SqlTypes.BOOLEAN)
    @Column(name = "IsPublished", nullable = false)
    private boolean isPublished;

    @Setter
    @JdbcTypeCode(SqlTypes.BOOLEAN)
    @Column(name = "IsBlocked", nullable = false)
    private boolean isBlocked;

    @Setter
    @JdbcTypeCode(SqlTypes.BOOLEAN)
    @Column(name = "IsDeleted", nullable = false)
    private boolean isDeleted;

    @ManyToOne(optional = false)
    @JoinColumn(name = "personId", nullable = false)
    private Person creator;

    @ManyToMany(mappedBy = "savedDecks")
    private List<Person> subscribers = new ArrayList<>();

    @Setter
    @JsonIgnore
    @Builder.Default
    @OneToMany(
            mappedBy = "deck",
            orphanRemoval = true,
            fetch=FetchType.EAGER
    )
    private List<Card> cards = new ArrayList<>();

    @Override
    public String toString() {
        return String.format("%s - %s", name, description);
    }

    @Override
    public boolean equals(Object obj) {
        return (this == obj) || ((obj instanceof Deck u) && (u.deckId == this.deckId));
    }

    @Override
    public int hashCode() {
        return this.deckId.hashCode();
    }
}
