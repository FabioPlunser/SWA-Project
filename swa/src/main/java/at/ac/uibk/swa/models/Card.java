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

    public Card(String frontText, String backText, boolean isFlipped, Deck deck) {
        this(null, frontText, backText, isFlipped, deck, new HashMap<>());
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

    @JsonIgnore
    @JoinColumn(name = "deck_id", nullable = false)
    @ManyToOne(optional = false)
    private Deck deck;

    @Getter(AccessLevel.NONE)
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
     * updates the learning progress to the values of the other progress
     * creates a new progress if no progress is found
     *
     * doing it like below is necessary to leave the id unchanged
     * updating by replacing the learning progress in the map of card will leave the old learning progress undeleted
     * database will be polluted over time
     *
     * unittest is in place to monitor the correct handling of this issue
     * @see at.ac.uibk.swa.service.card_service.CardServiceTestLearning#testLearnMultipleTimesMonitorLearningProgressEntities()
     *
     * @param person person for which the learning progress should be updated
     * @param learningProgress new learning progress from which the values are taken - does not need to be saved to repository
     */
    public void setLearningProgress(Person person, LearningProgress learningProgress) {
        // updating the learning progress like this is required because otherwise new learning progresses with new
        // ids are created each time and pollute the database
        //
        // unittest (CardServiceTestLearning.testLearnMultipleTimesMonitorLearningProgressEntities) is in place
        // to check this issue
        if (learningProgresses.containsKey(person)) {
            learningProgresses.get(person).update(learningProgress);
        } else {
            learningProgresses.put(person, learningProgress);
        }
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
