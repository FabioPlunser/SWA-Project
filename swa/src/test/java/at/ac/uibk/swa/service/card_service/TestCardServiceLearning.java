package at.ac.uibk.swa.service.card_service;

import at.ac.uibk.swa.models.*;
import at.ac.uibk.swa.repositories.LearningProgressRepository;
import at.ac.uibk.swa.service.CardService;
import at.ac.uibk.swa.service.PersonService;
import at.ac.uibk.swa.service.UserDeckService;
import at.ac.uibk.swa.util.MockAuthContext;
import at.ac.uibk.swa.util.StringGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class TestCardServiceLearning {
    @Autowired
    CardService cardService;
    @Autowired
    UserDeckService userDeckService;
    @Autowired
    PersonService personService;
    @Autowired
    LearningProgressRepository learningProgressRepository;

    private Person createUserAndLogin() {
        Person person = new Person(StringGenerator.username(), StringGenerator.email(), StringGenerator.password(), Set.of(Permission.USER));
        assertTrue(personService.create(person), "Unable to create user");
        return (Person) MockAuthContext.setLoggedInUser(person);
    }

    private Deck createDeck() {
        Deck deck = new Deck(StringGenerator.deckName(), StringGenerator.deckDescription());
        assertTrue(userDeckService.create(deck), "Unable to create deck");
        return deck;
    }

    private Card createCard(Deck deck) {
        Card card = new Card(StringGenerator.cardText(), StringGenerator.cardText(),false);
        assertTrue(cardService.create(card, deck.getDeckId()), "Unable to create card");
        return card;
    }
    
    @Test
    public void getInitialLearningProgress() {
        // given: a deck created by a user with a single card and another user
        Person person = createUserAndLogin();
        Card card = createCard(createDeck());

        // when: loading the learning progress for that card
        Optional<LearningProgress> maybeLearningProgress = cardService.getLearningProgress(card.getCardId());

        // then: no learning progress must be returned
        assertTrue(maybeLearningProgress.isEmpty(), "Got progress on a card that has never been learnt");
    }

    @Test
    public void getInitialCardsToLearn() {
        // given: a public deck created by a user with a number of cards and another user, subscribed to that deck
        int numberOfCards = 10;
        Person creator = createUserAndLogin();
        Deck deck = createDeck();
        List<Card> cards = new ArrayList<>();
        for (int i = 0; i < numberOfCards; i++) {
            cards.add(createCard(deck));
        }
        assertTrue(userDeckService.publish(deck.getDeckId()), "Unable to publish deck");
        Person person = createUserAndLogin();
        assertTrue(userDeckService.subscribe(deck.getDeckId()), "Unable to subscribe to deck");

        // when: loading all the cards to learn from that deck
        Optional<List<Card>> maybeCardsToLearn = cardService.getAllCardsToLearn(deck.getDeckId());

        // then: all cards from the deck must be loaded
        assertTrue(maybeCardsToLearn.isPresent(), "Unable to load cards");
        List<Card> cardsToLearn = maybeCardsToLearn.get();
        assertEquals(cards.size(), cardsToLearn.size(), "Got more/less cards than expected");
        for (Card card : cards) {
            assertTrue(cardsToLearn.contains(card), "Unable to find card");
        }
    }

    @Test
    public void learnUnlearntCard() {
        // given: a deck created by a user with one single card
        Person person = createUserAndLogin();
        Deck deck = createDeck();
        Card card = createCard(deck);

        // when: learning that one single card
        assertTrue(cardService.learn(card.getCardId(), 0), "Unable to learn card");

        // then: learning progress shall be updated
        Optional<LearningProgress> maybeLearningProgress = cardService.getLearningProgress(card.getCardId());
        assertTrue(maybeLearningProgress.isPresent(), "Did not find any learning progress");
        LearningProgress learningProgress = maybeLearningProgress.get();
        assertEquals(1, learningProgress.getRepetitions(), "Number of repetitions other than expected");
    }

    @Test
    public void learnMultipleTimesMonitorLearningProgressEntities() {
        // given: a deck created by a user with one single card
        Person person = createUserAndLogin();
        Deck deck = createDeck();
        Card card = createCard(deck);

        // when: learning the card n times
        long numberOfLearningProgressEntitiesBefore = learningProgressRepository.count();
        int numberOfLearningRepetitions = 10;
        for (int i = 0; i < numberOfLearningRepetitions; i++) {
            assertTrue(cardService.learn(card.getCardId(), 0), "Unable to learn card");
        }

        // then: only a single learning progress entity must be created
        long numberOfLearningProgressEntitiesAfter = learningProgressRepository.count();
        assertEquals(1, numberOfLearningProgressEntitiesAfter - numberOfLearningProgressEntitiesBefore, "Too many entities have been created");
    }
}
