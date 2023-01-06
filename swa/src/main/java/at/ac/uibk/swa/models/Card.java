package at.ac.uibk.swa.models;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.UnaryOperator;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "card")
public class Card implements Serializable {

    public Card(String frontText, String backText, boolean isFlipped) {
        this(null, frontText, backText, isFlipped, null, new HashMap<>());
    }

    @Id
    @Setter(AccessLevel.PRIVATE)
    @JdbcTypeCode(SqlTypes.NVARCHAR)
    @Column(name = "card_id", nullable = false)
    @GeneratedValue(strategy=GenerationType.AUTO)
    @JsonProperty(access = JsonProperty.Access.READ_WRITE)
    private UUID cardId;

    @Lob
    // @JdbcTypeCode(SqlTypes.NVARCHAR)
    @Column(name = "front_text", nullable = false)
    @JsonProperty(access = JsonProperty.Access.READ_WRITE)
    private String frontText;

    @Lob
    // @JdbcTypeCode(SqlTypes.NVARCHAR)
    @Column(name = "back_text", nullable = false)
    @JsonProperty(access = JsonProperty.Access.READ_WRITE)
    private String backText;

    @JdbcTypeCode(SqlTypes.BOOLEAN)
    @JsonAlias("isFlipped")
    @JsonProperty(access = JsonProperty.Access.READ_WRITE)
    @Column(name = "is_flipped", nullable = false)
    private boolean isFlipped;

    @JsonIgnore
    @JoinColumn(name = "deck_id", nullable = false)
    @ManyToOne(optional = false)
    private Deck deck;

    // NOTE: This JsonIgnore is ok, because this is a Map which we don't want to override in any case.
    @JsonIgnore
    @Setter(AccessLevel.PRIVATE)
    @OneToMany(fetch = FetchType.EAGER)
    @Builder.Default
    @JoinTable(
            name = "card_progress_mapping",
            joinColumns = {@JoinColumn(name = "card_id", referencedColumnName = "card_id")},
            inverseJoinColumns = {@JoinColumn(name = "progress_id", referencedColumnName = "progress_id")}
    )
    @MapKeyJoinColumn(name = "person_id")
    private Map<Person, LearningProgress> learningProgresses = new HashMap<>();

    @JsonIgnore
    public Optional<LearningProgress> getLearningProgress(Person person) {
        return Optional.ofNullable(this.learningProgresses.getOrDefault(person, null));
    }

    /**
     * Updates the LearningProgress associated with the given Person in the Card.
     * If no LearningProgress is found, a new one is created.
     *
     * @implNote The ID of the LearningProgress is preserved.
     * @param person person for which the learning progress should be updated
     * @param func A Function to compute the new LearningProgress.
     * @return The new LearningProgress
     */
    public LearningProgress updateLearningProgress(Person person, UnaryOperator<LearningProgress> func) {
        return learningProgresses.compute(person, (p, lp) -> {
            // If the User has not learned this card, create an empty LearningProgress.
            if (lp == null)
                return func.apply(new LearningProgress());

            // Preserve the ID of the LearningProgress.
            UUID oldId = lp.getLearningProgressId();

            // If the card has already been learned, pass in the old Learning Progress.
            LearningProgress newLp = func.apply(lp);

            // Set the old ID
            newLp.setLearningProgressId(oldId);

            return newLp;
        });
    }

    @Override
    public String toString() {
        String front = this.isFlipped ? this.frontText : this.backText;
        String back = !this.isFlipped ? this.frontText : this.backText;

        return String.format("%s - %s", front, back);
    }

    @Override
    public boolean equals(Object o) {
        return (this == o) || ((o instanceof Card c) && (this.cardId != null) && (this.cardId.equals(c.cardId)));
    }

    @Override
    public int hashCode() {
        // NOTE: This will intentionally throw an Exception if cardId is null.
        return this.cardId.hashCode();
    }
}
