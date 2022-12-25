package at.ac.uibk.swa.service.card_service;

import at.ac.uibk.swa.models.*;
import at.ac.uibk.swa.service.CardService;
import at.ac.uibk.swa.service.PersonService;
import at.ac.uibk.swa.service.UserDeckService;
import at.ac.uibk.swa.util.StringGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
public class CardServiceTestLearning {
    @Autowired
    CardService cardService;
    @Autowired
    UserDeckService userDeckService;
    @Autowired
    PersonService personService;

    private Person createUser(String username) {
        Person person = new Person(username, StringGenerator.email(), StringGenerator.password(), Set.of(Permission.USER));
        assertTrue(personService.create(person), "Unable to create user");
        return person;
    }

    private Card createCard(String deckName, String creatorName) {
        Person creator = createUser(creatorName);
        Deck deck = new Deck(deckName, StringGenerator.deckDescription(), creator);
        assertTrue(userDeckService.create(deck), "Unable to create deck");
        Card card = new Card(StringGenerator.cardText(), StringGenerator.cardText(), false, deck);
        assertTrue(cardService.create(card), "Unable to create card");
        return card;
    }
    
    @Test
    public void testGetInitialLearningProgressOfDeck() {
        // given: a deck created by a user with a single card and another user
        int numberOfCards = 1;
        Card card = createCard("deck-testGetInitialLearningProgressOfDeck", "creator-testGetInitialLearningProgressOfDeck");
        Person person = createUser("person-testGetInitialLearningProgressOfDeck");

        // when: loading the learning progress for that card
        Optional<LearningProgress> maybeLearningProgress = cardService.getLearningProgress(card, person);

        // then: no learning progress must be returned
        assertTrue(maybeLearningProgress.isEmpty(), "Got progress on a card that has never been learnt");
    }
}
