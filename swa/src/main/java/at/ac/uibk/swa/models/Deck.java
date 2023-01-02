package at.ac.uibk.swa.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
@Table(name = "deck")
public class Deck implements Serializable {

    public Deck(String name, String description) {
        this(null, name, description, false, false, false, null, new ArrayList<>(), new ArrayList<>());
    }

    public Deck(String name, String description, Person creator) {
        this(null, name, description, false, false, false, creator, new ArrayList<>(), new ArrayList<>());
    }

    @Id
    @JdbcTypeCode(SqlTypes.NVARCHAR)
    @Column(name = "deck_id", nullable = false)
    @GeneratedValue(strategy=GenerationType.AUTO)
    private UUID deckId;

    @Setter
    @JdbcTypeCode(SqlTypes.NVARCHAR)
    @Column(name = "name", nullable = false)
    private String name;

    @Setter
    @Lob
    // @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "description", nullable = false)
    private String description;

    @Setter
    @JdbcTypeCode(SqlTypes.BOOLEAN)
    @Column(name = "is_published", nullable = false)
    private boolean isPublished;

    @Setter
    @JdbcTypeCode(SqlTypes.BOOLEAN)
    @Column(name = "is_blocked", nullable = false)
    private boolean isBlocked;

    @Setter
    @JdbcTypeCode(SqlTypes.BOOLEAN)
    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted;

    @Setter
    @ManyToOne(optional = false)
    @JoinColumn(name = "creator_id", nullable = false)
    private Person creator;

    @JsonIgnoreProperties(allowSetters = true)
    @Builder.Default
    @Setter
    @OneToMany(
            mappedBy = "deck",
            orphanRemoval = true,
            fetch=FetchType.EAGER
    )
    private List<Card> cards = new ArrayList<>();

    @Builder.Default
    @ManyToMany(mappedBy = "savedDecks", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Person> subscribedPersons = new ArrayList<>();

    @Override
    public String toString() {
        return String.format("%s - %s", name, description);
    }

    @Override
    public boolean equals(Object o) {
        return (this == o) || ((o instanceof Deck d) && (this.deckId.equals(d.deckId)));
    }

    @Override
    public int hashCode() {
        return deckId.hashCode();
    }
}
