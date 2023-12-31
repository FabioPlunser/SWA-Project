package at.ac.uibk.swa.models;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
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
    @JsonProperty(access = JsonProperty.Access.READ_WRITE)
    private UUID deckId;

    @JdbcTypeCode(SqlTypes.NVARCHAR)
    @Column(name = "name", nullable = false)
    @JsonProperty(access = JsonProperty.Access.READ_WRITE)
    private String name;

    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    @Column(name = "description", nullable = false)
    @JsonProperty(access = JsonProperty.Access.READ_WRITE)
    private String description;

    @JdbcTypeCode(SqlTypes.BOOLEAN)
    @JsonAlias("isPublished")
    @JsonProperty(access = JsonProperty.Access.READ_WRITE)
    @Column(name = "is_published", nullable = false)
    private boolean isPublished;

    @JdbcTypeCode(SqlTypes.BOOLEAN)
    @JsonAlias("isBlocked")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Column(name = "is_blocked", nullable = false)
    private boolean isBlocked;

    @JdbcTypeCode(SqlTypes.BOOLEAN)
    @JsonAlias("isDeleted")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted;

    @JsonIgnore
    @ManyToOne(optional = false)
    @JoinColumn(name = "creator_id", nullable = false)
    private Person creator;

    @Builder.Default
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @OneToMany(
            mappedBy = "deck",
            orphanRemoval = true,
            fetch=FetchType.EAGER
    )
    private List<Card> cards = new ArrayList<>();

    @JsonIgnore
    @Builder.Default
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "person_saved_deck",
            joinColumns = @JoinColumn(name = "deck_id", referencedColumnName = "deck_id"),
            inverseJoinColumns = @JoinColumn(name = "person_id", referencedColumnName = "person_id"))
    private List<Person> subscribedPersons = new ArrayList<>();

    @JsonIgnore
    public boolean isCreator(Person person) {
        return this.creator.equals(person);
    }

    public boolean addSubscriber(Person person) {
        return this.subscribedPersons.add(person);
    }

    public boolean removeSubscriber(Person person) {
        return this.subscribedPersons.remove(person);
    }

    @Override
    public String toString() {
        return "Deck{" +
                "deckId=" + deckId +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", isPublished=" + isPublished +
                ", isBlocked=" + isBlocked +
                ", isDeleted=" + isDeleted +
                '}';
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
