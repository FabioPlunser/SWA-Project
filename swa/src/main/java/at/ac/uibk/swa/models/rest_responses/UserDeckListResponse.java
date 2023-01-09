package at.ac.uibk.swa.models.rest_responses;

import at.ac.uibk.swa.config.person_authentication.AuthContext;
import at.ac.uibk.swa.models.Deck;
import at.ac.uibk.swa.models.LearningProgress;
import at.ac.uibk.swa.models.Person;
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

    public UserDeckListResponse(List<Deck> decks) {
        this(decks, AuthContext.getCurrentPerson().get());
    }

    public UserDeckListResponse(List<Deck> decks, Person person) {
        super(decks.stream().map(deck -> new UserDeckInfo(deck, person)).toList());
    }

    @Getter
    public static class UserDeckInfo implements Serializable {
        private Deck deck;
        private int numCards;
        private long numCardsToLearn;

        public UserDeckInfo(Deck deck) throws CredentialException {
            this(deck, AuthContext.getCurrentPerson().orElseThrow(() -> new CredentialException()));
        }

        public UserDeckInfo(Deck deck, Person person) {
            this.deck = deck;
            this.numCards = this.deck.getCards().size();
            LocalDateTime now = LocalDateTime.now();
            this.numCardsToLearn = deck.getCards().stream()
                    .map(card -> card.getLearningProgress(person))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .map(LearningProgress::getNextLearn)
                    .filter(nl -> nl.isAfter(now))
                    .count();
        }
    }
}