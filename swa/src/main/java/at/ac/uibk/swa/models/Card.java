package at.ac.uibk.swa.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

@Getter
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
    @JdbcTypeCode(SqlTypes.NVARCHAR)
    @Column(name = "card_id", nullable = false)
    @GeneratedValue(strategy=GenerationType.AUTO)
    private UUID cardId;

    @Setter
    @Lob
    // @JdbcTypeCode(SqlTypes.NVARCHAR)
    @Column(name = "front_text", nullable = false)
    private String frontText;

    @Setter
    @Lob
    // @JdbcTypeCode(SqlTypes.NVARCHAR)
    @Column(name = "back_text", nullable = false)
    private String backText;

    @Setter
    @Column(name = "is_flipped", nullable = false)
    @JdbcTypeCode(SqlTypes.BOOLEAN)
    private boolean isFlipped;

    @Setter
    @JsonIgnore
    @JoinColumn(name = "deck_id", nullable = false)
    @ManyToOne(optional = false)
    private Deck deck;

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    @JsonIgnore
    @OneToMany(fetch = FetchType.EAGER)
    @Builder.Default
    @JoinTable(
            name = "card_progress_mapping",
            joinColumns = {@JoinColumn(name = "card_id", referencedColumnName = "card_id")},
            inverseJoinColumns = {@JoinColumn(name = "progress_id", referencedColumnName = "progress_id")}
    )

    @MapKeyJoinColumn(name = "person_id")
    private Map<Person, LearningProgress> learningProgresses = new HashMap<>();

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
    public LearningProgress updateLearningProgress(Person person, Function<LearningProgress, LearningProgress> func) {
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
        return cardId.toString();
    }

    @Override
    public boolean equals(Object o) {
        return (this == o) || ((o instanceof Card c) && (this.cardId.equals(c.cardId)));
    }

    @Override
    public int hashCode() {
        return cardId.hashCode();
    }
}
