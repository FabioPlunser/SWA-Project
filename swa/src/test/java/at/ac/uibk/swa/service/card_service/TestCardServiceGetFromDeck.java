package at.ac.uibk.swa.service.card_service;

import at.ac.uibk.swa.models.Card;
import at.ac.uibk.swa.models.Deck;
import at.ac.uibk.swa.models.Permission;
import at.ac.uibk.swa.models.Person;
import at.ac.uibk.swa.service.AdminDeckService;
import at.ac.uibk.swa.service.CardService;
import at.ac.uibk.swa.service.PersonService;
import at.ac.uibk.swa.service.UserDeckService;
import at.ac.uibk.swa.util.MockAuthContext;
import at.ac.uibk.swa.util.StringGenerator;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
public class TestCardServiceGetFromDeck {
    @Autowired
    private UserDeckService userDeckService;
    @Autowired
    private AdminDeckService adminDeckService;
    @Autowired
    private CardService cardService;
    @Autowired
    private PersonService personService;

    private Person createUserAndLogin() {
        Person person = new Person(StringGenerator.username(), StringGenerator.email(), StringGenerator.password(), Set.of(Permission.USER));
        assertTrue(personService.create(person), "Unable to create user");
        return (Person) MockAuthContext.setLoggedInUser(person);
    }

    private Person createAdminAndLogin() {
        Person person = new Person(StringGenerator.username(), StringGenerator.email(), StringGenerator.password(), Set.of(Permission.ADMIN));
        assertTrue(personService.create(person), "Unable to create user");
        return (Person) MockAuthContext.setLoggedInUser(person);
    }

    private Deck createDeck() {
        Deck deck = new Deck(StringGenerator.deckName(), StringGenerator.deckDescription());
        assertTrue(userDeckService.create(deck), "Unable to create deck");
        return deck;
    }

    @Test
    public void getCardsFromOwnedDeck() {
        // given: a user, that created a deck with multiple cards
        int numCardsPerDeck = 10;
        Person person = createUserAndLogin();
        Deck deck = createDeck();
        List<Card> cards = new ArrayList<>();
        for (int i = 0; i < numCardsPerDeck; i++) {
            Card card = new Card(StringGenerator.cardText(), StringGenerator.cardText(),false);
            assertTrue(cardService.create(card, deck.getDeckId()), "Unable to create card");
            cards.add(card);
        }

        // when: retrieving all cards for that user and deck
        Optional<List<Card>> maybeLoadedCards = cardService.getAllCards(deck.getDeckId());

        // then: all cards must be loaded again
        assertTrue(maybeLoadedCards.isPresent(), "Unable to load cards");
        List<Card> loadedCards = maybeLoadedCards.get();
        assertEquals(cards.size(), loadedCards.size(), "Got wrong number of cards");
        for (Card card : cards) {
            assertTrue(loadedCards.contains(card), "Unable to find a card");
        }
    }

    @Test
    public void getCardsFromOwnedBlockedDeck() {
        // given: a user, that created a deck with multiple cards, where the deck got blocked after creation
        int numCardsPerDeck = 10;
        Person person = createUserAndLogin();
        Deck deck = createDeck();
        List<Card> cards = new ArrayList<>();
        for (int i = 0; i < numCardsPerDeck; i++) {
            Card card = new Card(StringGenerator.cardText(), StringGenerator.cardText(),false);
            assertTrue(cardService.create(card, deck.getDeckId()), "Unable to create card");
            cards.add(card);
        }
        assertTrue(adminDeckService.block(deck.getDeckId()), "Unable to block deck");

        // when: retrieving all cards for that user and deck
        Optional<List<Card>> maybeLoadedCards = cardService.getAllCards(deck.getDeckId());

        // then: no cards must be loaded
        assertTrue(maybeLoadedCards.isPresent(), "Unable to load cards");
        List<Card> loadedCards = maybeLoadedCards.get();
        assertEquals(0, loadedCards.size(), "Got wrong number of cards");
    }

    @Test
    public void getCardsFromOwnedDeletedDeck() {
        // given: a user, that created a deck with multiple cards, where the deck got deleted after creation
        int numCardsPerDeck = 10;
        Person person = createUserAndLogin();
        Deck deck = createDeck();
        List<Card> cards = new ArrayList<>();
        for (int i = 0; i < numCardsPerDeck; i++) {
            Card card = new Card(StringGenerator.cardText(), StringGenerator.cardText(),false);
            assertTrue(cardService.create(card, deck.getDeckId()), "Unable to create card");
            cards.add(card);
        }
        assertTrue(userDeckService.delete(deck.getDeckId()), "Unable to delete deck");

        // when: retrieving all cards for that user and deck
        Optional<List<Card>> maybeLoadedCards = cardService.getAllCards(deck.getDeckId());

        // then: no cards must be loaded
        assertTrue(maybeLoadedCards.isPresent(), "Unable to load cards");
        List<Card> loadedCards = maybeLoadedCards.get();
        assertEquals(0, loadedCards.size(), "Got wrong number of cards");
    }

    @Test
    public void getCardsFromSubscribedPublishedDeck() {
        // given: a user, that subscribed to a deck with multiple cards
        int numCardsPerDeck = 10;
        Person creator = createUserAndLogin();
        Deck deck = createDeck();
        List<Card> cards = new ArrayList<>();
        for (int i = 0; i < numCardsPerDeck; i++) {
            Card card = new Card(StringGenerator.cardText(), StringGenerator.cardText(),false);
            assertTrue(cardService.create(card, deck.getDeckId()), "Unable to create card");
            cards.add(card);
        }
        assertTrue(userDeckService.publish(deck.getDeckId()), "Unable to publish deck");
        Person person = createUserAndLogin();
        assertTrue(userDeckService.subscribe(deck.getDeckId()), "Unable to subscribe to deck");

        // when: retrieving all cards for that user and deck
        Optional<List<Card>> maybeLoadedCards = cardService.getAllCards(deck.getDeckId());

        // then: all cards must be loaded again
        assertTrue(maybeLoadedCards.isPresent(), "Unable to load cards");
        List<Card> loadedCards = maybeLoadedCards.get();
        assertEquals(cards.size(), loadedCards.size(), "Got wrong number of cards");
        for (Card card : cards) {
            assertTrue(loadedCards.contains(card), "Unable to find a card");
        }
    }

    @Test
    public void getCardsFromSubscribedUnpublishedDeck() {
        // given: a user, that subscribed to a deck with multiple cards, but the deck was unpublished after subscription
        int numCardsPerDeck = 10;
        Person creator = createUserAndLogin();
        Deck deck = createDeck();
        List<Card> cards = new ArrayList<>();
        for (int i = 0; i < numCardsPerDeck; i++) {
            Card card = new Card(StringGenerator.cardText(), StringGenerator.cardText(),false);
            assertTrue(cardService.create(card, deck.getDeckId()), "Unable to create card");
            cards.add(card);
        }
        assertTrue(userDeckService.publish(deck.getDeckId()), "Unable to publish deck");
        Person person = createUserAndLogin();
        assertTrue(userDeckService.subscribe(deck.getDeckId()), "Unable to subscribe to deck");
        MockAuthContext.setLoggedInUser(creator);
        assertTrue(userDeckService.unpublish(deck.getDeckId()), "Unable to unpublish deck");
        MockAuthContext.setLoggedInUser(person);

        // when: retrieving all cards for that user and deck
        Optional<List<Card>> maybeLoadedCards = cardService.getAllCards(deck.getDeckId());

        // then: no cards must be loaded
        assertTrue(maybeLoadedCards.isPresent(), "Unable to load cards");
        List<Card> loadedCards = maybeLoadedCards.get();
        assertEquals(0, loadedCards.size(), "Got wrong number of cards");
    }

    @Test
    public void getCardsFromSubscribedBlockedDeck() {
        // given: a user, that subscribed to a deck with multiple cards, but the deck was blocked after subscription
        int numCardsPerDeck = 10;
        Person creator = createUserAndLogin();
        Deck deck = createDeck();
        List<Card> cards = new ArrayList<>();
        for (int i = 0; i < numCardsPerDeck; i++) {
            Card card = new Card(StringGenerator.cardText(), StringGenerator.cardText(),false);
            assertTrue(cardService.create(card, deck.getDeckId()), "Unable to create card");
            cards.add(card);
        }
        assertTrue(userDeckService.publish(deck.getDeckId()), "Unable to publish deck");
        Person person = createUserAndLogin();
        assertTrue(userDeckService.subscribe(deck.getDeckId()), "Unable to subscribe to deck");
        assertTrue(adminDeckService.block(deck.getDeckId()), "Unable to block deck");

        // when: retrieving all cards for that user and deck
        Optional<List<Card>> maybeLoadedCards = cardService.getAllCards(deck.getDeckId());

        // then: no cards must be loaded
        assertTrue(maybeLoadedCards.isPresent(), "Unable to load cards");
        List<Card> loadedCards = maybeLoadedCards.get();
        assertEquals(0, loadedCards.size(), "Got wrong number of cards");
    }

    @Test
    public void getCardsFromSubscribedDeletedDeck() {
        // given: a user, that subscribed to a deck with multiple cards, but the deck was blocked after subscription
        int numCardsPerDeck = 10;
        Person creator = createUserAndLogin();
        Deck deck = createDeck();
        List<Card> cards = new ArrayList<>();
        for (int i = 0; i < numCardsPerDeck; i++) {
            Card card = new Card(StringGenerator.cardText(), StringGenerator.cardText(),false);
            assertTrue(cardService.create(card, deck.getDeckId()), "Unable to create card");
            cards.add(card);
        }
        assertTrue(userDeckService.publish(deck.getDeckId()), "Unable to publish deck");
        Person person = createUserAndLogin();
        assertTrue(userDeckService.subscribe(deck.getDeckId()), "Unable to subscribe to deck");
        MockAuthContext.setLoggedInUser(creator);
        assertTrue(userDeckService.delete(deck.getDeckId()), "Unable to delete deck");
        MockAuthContext.setLoggedInUser(person);

        // when: retrieving all cards for that user and deck
        Optional<List<Card>> maybeLoadedCards = cardService.getAllCards(deck.getDeckId());

        // then: no cards must be loaded
        assertTrue(maybeLoadedCards.isPresent(), "Unable to load cards");
        List<Card> loadedCards = maybeLoadedCards.get();
        assertEquals(0, loadedCards.size(), "Got wrong number of cards");
    }

    @Test
    public void getDeletedCardsFromDeck() {
        // given: a user, that a deck with multiple cards and deleted a card after creation
        int numCardsPerDeck = 10;
        Person creator = createUserAndLogin();
        Deck deck = createDeck();
        List<Card> cards = new ArrayList<>();
        for (int i = 0; i < numCardsPerDeck; i++) {
            Card card = new Card(StringGenerator.cardText(), StringGenerator.cardText(),false);
            assertTrue(cardService.create(card, deck.getDeckId()), "Unable to create card");
            cards.add(card);
        }
        assertTrue(userDeckService.publish(deck.getDeckId()), "Unable to publish deck");
        Person person = createUserAndLogin();
        assertTrue(userDeckService.subscribe(deck.getDeckId()), "Unable to subscribe to deck");
        MockAuthContext.setLoggedInUser(creator);
        assertTrue(cardService.delete(cards.get(0).getCardId()), "Unable to delete card");
        cards.remove(0);
        MockAuthContext.setLoggedInUser(person);

        // when: retrieving all cards for that user and deck
        Optional<List<Card>> maybeLoadedCards = cardService.getAllCards(deck.getDeckId());

        // then: all cards, except the deleted card must be loaded
        assertTrue(maybeLoadedCards.isPresent(), "Unable to load cards");
        List<Card> loadedCards = maybeLoadedCards.get();
        assertEquals(cards.size(), loadedCards.size(), "Got wrong number of cards");
        for (Card card : cards) {
            assertTrue(loadedCards.contains(card), "Unable to find card");
        }
    }

    @Test
    public void getCardsFromUnpublishedDeckAsAdmin() {
        // given: an admin and an unpublished deck created by a user
        int numCardsPerDeck = 10;
        Person creator = createUserAndLogin();
        Deck deck = createDeck();
        List<Card> cards = new ArrayList<>();
        for (int i = 0; i < numCardsPerDeck; i++) {
            Card card = new Card(StringGenerator.cardText(), StringGenerator.cardText(),false);
            assertTrue(cardService.create(card, deck.getDeckId()), "Unable to create card");
            cards.add(card);
        }
        Person admin = createAdminAndLogin();

        // when: retrieving all cards for the admin and deck
        Optional<List<Card>> maybeLoadedCards = cardService.getAllCards(deck.getDeckId());

        // then: all cards must be loaded
        assertTrue(maybeLoadedCards.isPresent(), "Unable to load cards");
        List<Card> loadedCards = maybeLoadedCards.get();
        assertEquals(cards.size(), loadedCards.size(), "Got wrong number of cards");
        for (Card card : cards) {
            assertTrue(loadedCards.contains(card), "Unable to find card");
        }
    }

    @Test
    public void getCardsFromPublishedDeckAsAdmin() {
        // given: an admin and an published deck created by a user
        int numCardsPerDeck = 10;
        Person creator = createUserAndLogin();
        Deck deck = createDeck();
        List<Card> cards = new ArrayList<>();
        for (int i = 0; i < numCardsPerDeck; i++) {
            Card card = new Card(StringGenerator.cardText(), StringGenerator.cardText(),false);
            assertTrue(cardService.create(card, deck.getDeckId()), "Unable to create card");
            cards.add(card);
        }
        assertTrue(userDeckService.publish(deck.getDeckId()), "Unable to publish deck");
        Person admin = createAdminAndLogin();

        // when: retrieving all cards for the admin and deck
        Optional<List<Card>> maybeLoadedCards = cardService.getAllCards(deck.getDeckId());

        // then: all cards must be loaded
        assertTrue(maybeLoadedCards.isPresent(), "Unable to load cards");
        List<Card> loadedCards = maybeLoadedCards.get();
        assertEquals(cards.size(), loadedCards.size(), "Got wrong number of cards");
        for (Card card : cards) {
            assertTrue(loadedCards.contains(card), "Unable to find card");
        }
    }

    @Test
    public void getCardsFromBlockedDeckAsAdmin() {
        // given: an admin and a blocked deck created by a user
        int numCardsPerDeck = 10;
        Person creator = createUserAndLogin();
        Deck deck = createDeck();
        List<Card> cards = new ArrayList<>();
        for (int i = 0; i < numCardsPerDeck; i++) {
            Card card = new Card(StringGenerator.cardText(), StringGenerator.cardText(),false);
            assertTrue(cardService.create(card, deck.getDeckId()), "Unable to create card");
            cards.add(card);
        }
        Person admin = createAdminAndLogin();
        assertTrue(adminDeckService.block(deck.getDeckId()), "Unable to block deck");

        // when: retrieving all cards for the admin and deck
        Optional<List<Card>> maybeLoadedCards = cardService.getAllCards(deck.getDeckId());

        // then: all cards must be loaded
        assertTrue(maybeLoadedCards.isPresent(), "Unable to load cards");
        List<Card> loadedCards = maybeLoadedCards.get();
        assertEquals(cards.size(), loadedCards.size(), "Got wrong number of cards");
        for (Card card : cards) {
            assertTrue(loadedCards.contains(card), "Unable to find card");
        }
    }

    @Test
    public void getCardsFromDeletedDeckAsAdmin() {
        // given: an admin and a deleted deck created by a user
        int numCardsPerDeck = 10;
        Person creator = createUserAndLogin();
        Deck deck = createDeck();
        List<Card> cards = new ArrayList<>();
        for (int i = 0; i < numCardsPerDeck; i++) {
            Card card = new Card(StringGenerator.cardText(), StringGenerator.cardText(),false);
            assertTrue(cardService.create(card, deck.getDeckId()), "Unable to create card");
            cards.add(card);
        }
        assertTrue(userDeckService.delete(deck.getDeckId()), "Unable to delete deck");
        Person admin = createAdminAndLogin();

        // when: retrieving all cards for that user and deck
        Optional<List<Card>> maybeLoadedCards = cardService.getAllCards(deck.getDeckId());

        // then: no cards must be loaded
        assertTrue(maybeLoadedCards.isPresent(), "Unable to load cards");
        List<Card> loadedCards = maybeLoadedCards.get();
        assertEquals(0, loadedCards.size(), "Got wrong number of cards");
    }

    @Test
    public void getDeletedCardsFromDeckAsAdmin() {
        // given: an admin and a deck created by a user, where a card was deleted
        int numCardsPerDeck = 10;
        Person creator = createUserAndLogin();
        Deck deck = createDeck();
        List<Card> cards = new ArrayList<>();
        for (int i = 0; i < numCardsPerDeck; i++) {
            Card card = new Card(StringGenerator.cardText(), StringGenerator.cardText(),false);
            assertTrue(cardService.create(card, deck.getDeckId()), "Unable to create card");
            cards.add(card);
        }
        assertTrue(cardService.delete(cards.get(0).getCardId()), "Unable to delete card");
        cards.remove(0);
        Person admin = createAdminAndLogin();

        // when: retrieving all cards for the admin and deck
        Optional<List<Card>> maybeLoadedCards = cardService.getAllCards(deck.getDeckId());

        // then: all cards, except the deleted card must be loaded
        assertTrue(maybeLoadedCards.isPresent(), "Unable to load cards");
        List<Card> loadedCards = maybeLoadedCards.get();
        assertEquals(cards.size(), loadedCards.size(), "Got wrong number of cards");
        for (Card card : cards) {
            assertTrue(loadedCards.contains(card), "Unable to find card");
        }
    }

    @Test
    public void getCardsToLearnAfterAddingCards() {
        // given: a deck with a card, where all cards are learnt
        Person creator = createUserAndLogin();
        Deck deck = createDeck();
        Card originalCard = new Card(StringGenerator.cardText(), StringGenerator.cardText(), false);
        cardService.create(originalCard, deck.getDeckId());
        cardService.learn(originalCard.getCardId(), 5);

        // when: adding a new card and getting all cards to learn
        Card newCard = new Card(StringGenerator.cardText(), StringGenerator.cardText(), false);
        cardService.create(newCard, deck.getDeckId());
        Optional<List<Card>> maybeCardsToLearn = cardService.getAllCardsToLearn(deck.getDeckId());

        // then: only the new cards must be returned
        assertTrue(maybeCardsToLearn.isPresent(), "Unable to load cards to learn");
        List<Card> cardsToLearn = maybeCardsToLearn.get();
        assertEquals(1, cardsToLearn.size(), "Got a different number of cards than expected");
        assertTrue(cardsToLearn.contains(newCard), "Did not find the new card");
    }

    @Test
    public void getCardsToLearnAfterAddingCardsViaUpdateDeck() {
        // given: a deck with a card, where all cards are learnt
        Person creator = createUserAndLogin();
        Deck deck = createDeck();
        Card originalCard = new Card(StringGenerator.cardText(), StringGenerator.cardText(), false);
        cardService.create(originalCard, deck.getDeckId());
        cardService.learn(originalCard.getCardId(), 5);

        // when: adding a new card via update deck and getting all cards to learn
        Card newCard = new Card(StringGenerator.cardText(), StringGenerator.cardText(), false);
        List<Card> allCards = new ArrayList<>();
        allCards.add(originalCard);
        allCards.add(newCard);
        deck.setCards(allCards);
        userDeckService.update(deck, true);
        Optional<List<Card>> maybeCardsToLearn = cardService.getAllCardsToLearn(deck.getDeckId());

        // then: only the new cards must be returned
        assertTrue(maybeCardsToLearn.isPresent(), "Unable to load cards to learn");
        List<Card> cardsToLearn = maybeCardsToLearn.get();
        assertEquals(1, cardsToLearn.size(), "Got a different number of cards than expected");
        assertTrue(cardsToLearn.contains(newCard), "Did not find the new card");
    }
}
