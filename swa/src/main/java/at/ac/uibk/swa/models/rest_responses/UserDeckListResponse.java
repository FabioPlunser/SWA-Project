package at.ac.uibk.swa.models.rest_responses;

import at.ac.uibk.swa.config.jwt_authentication.AuthContext;
import at.ac.uibk.swa.models.Deck;
import at.ac.uibk.swa.models.LearningProgress;
import at.ac.uibk.swa.models.Person;
import at.ac.uibk.swa.util.DoubleCounter;
import at.ac.uibk.swa.util.DoublePredicateCountingCollector;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import javax.security.auth.login.CredentialException;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Getter
@SuperBuilder
public class UserDeckListResponse extends ListResponse<UserDeckListResponse.UserDeckInfo> implements Serializable {
    @Override
    @JsonInclude
    public String getType() { return "UserDeckList"; }

    public UserDeckListResponse(List<Deck> decks) {
        this(decks, AuthContext.getCurrentPerson().get());
    }

    public UserDeckListResponse(List<Deck> decks, Person person) {
        super(decks.stream().map(deck -> new UserDeckInfo(deck, person)).toList());
    }

    @Getter
    public static class UserDeckInfo implements Serializable {
        @JsonUnwrapped
        private Deck deck;
        private int numCards;
        private long numCardsToRepeat;
        private long numNotLearnedCards;

        public UserDeckInfo(Deck deck) throws CredentialException {
            this(deck, AuthContext.getCurrentPerson().orElseThrow(() -> new CredentialException()));
        }

        public UserDeckInfo(Deck deck, Person person) {
            this.deck = deck;
            this.numCards = this.deck.getCards().size();

            LocalDateTime now = LocalDateTime.now();
            DoubleCounter counter = deck.getCards().stream()
                    .map(card -> card.getLearningProgress(person))
                    .collect(new DoublePredicateCountingCollector<Optional<LearningProgress>>(
                            Optional::isEmpty,
                            lp -> lp.map(learningProgress -> learningProgress.getNextLearn().isBefore(now)).orElse(false)
                    ));

            this.numNotLearnedCards = counter.matchesFirst;
            this.numCardsToRepeat = counter.matchesSecond;
        }
    }
}