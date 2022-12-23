package at.ac.uibk.swa.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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

    @Override
    public String toString() {
        String f = !isFlipped ? this.frontText : this.backText;
        String b =  isFlipped ? this.frontText : this.backText;
        return String.format("Card: %s - %s", f, b);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Card card = (Card) o;

        return cardId.equals(card.cardId);
    }

    @Override
    public int hashCode() {
        return cardId.hashCode();
    }
}
