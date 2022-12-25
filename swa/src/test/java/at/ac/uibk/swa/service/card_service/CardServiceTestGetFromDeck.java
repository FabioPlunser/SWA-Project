package at.ac.uibk.swa.service.card_service;

import at.ac.uibk.swa.models.Card;
import at.ac.uibk.swa.models.Deck;
import at.ac.uibk.swa.models.Permission;
import at.ac.uibk.swa.models.Person;
import at.ac.uibk.swa.service.AdminDeckService;
import at.ac.uibk.swa.service.CardService;
import at.ac.uibk.swa.service.PersonService;
import at.ac.uibk.swa.service.UserDeckService;
import at.ac.uibk.swa.util.StringGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
public class CardServiceTestGetFromDeck {
    @Autowired
    private UserDeckService userDeckService;
    @Autowired
    private AdminDeckService adminDeckService;
    @Autowired
    private CardService cardService;
    @Autowired
    private PersonService personService;

    private Person createUser(String username) {
        Person person = new Person(username, StringGenerator.email(), StringGenerator.password(), Set.of(Permission.USER));
        assertTrue(personService.create(person), "Unable to create user");
        return person;
    }

    private Person createAdmin(String username) {
        Person person = new Person(username, StringGenerator.email(), StringGenerator.password(), Set.of(Permission.ADMIN));
        assertTrue(personService.create(person), "Unable to create user");
        return person;
    }

    private Deck createDeck(String name, Person creator) {
        Deck deck = new Deck(name, StringGenerator.deckDescription(), creator);
        assertTrue(userDeckService.create(deck), "Unable to create deck");
        return deck;
    }

    @Test
    public void testGetCardsFromOwnedDeck() {
        // given: a user, that created a deck with multiple cards
        int numCardsPerDeck = 10;
        Person person = createUser("person-testGetCardsFromDeckOwned");
        Deck deck = createDeck("deck-testGetCardsFromDeckOwned", person);
        List<Card> cards = new ArrayList<>();
        for (int i = 0; i < numCardsPerDeck; i++) {
            Card card = new Card(StringGenerator.cardText(), StringGenerator.cardText(),false, deck);
            assertTrue(cardService.create(card), "Unable to create card");
            cards.add(card);
        }

        // when: retrieving all cards for that user and deck
        List<Card> loadedCards = cardService.getAllCards(deck, person);

        // then: all cards must be loaded again
        assertEquals(cards.size(), loadedCards.size(), "Got wrong number of cards");
        for (Card card : cards) {
            assertTrue(loadedCards.contains(card), "Unable to find a card");
        }
    }

    @Test
    public void testGetCardsFromOwnedBlockedDeck() {
        // given: a user, that created a deck with multiple cards, where the deck got blocked after creation
        int numCardsPerDeck = 10;
        Person person = createUser("person-testGetCardsFromDeckOwnedBlocked");
        Deck deck = createDeck("deck-testGetCardsFromDeckOwnedBlocked", person);
        List<Card> cards = new ArrayList<>();
        for (int i = 0; i < numCardsPerDeck; i++) {
            Card card = new Card(StringGenerator.cardText(), StringGenerator.cardText(),false, deck);
            assertTrue(cardService.create(card), "Unable to create card");
            cards.add(card);
        }
        assertTrue(adminDeckService.block(deck), "Unable to block deck");

        // when: retrieving all cards for that user and deck
        List<Card> loadedCards = cardService.getAllCards(deck, person);

        // then: no cards must be loaded
        assertEquals(0, loadedCards.size(), "Got wrong number of cards");
    }

    @Test
    public void testGetCardsFromOwnedDeletedDeck() {
        // given: a user, that created a deck with multiple cards, where the deck got deleted after creation
        int numCardsPerDeck = 10;
        Person person = createUser("person-testGetCardsFromDeckOwnedDeleted");
        Deck deck = createDeck("deck-testGetCardsFromDeckOwnedDeleted", person);
        List<Card> cards = new ArrayList<>();
        for (int i = 0; i < numCardsPerDeck; i++) {
            Card card = new Card(StringGenerator.cardText(), StringGenerator.cardText(),false, deck);
            assertTrue(cardService.create(card), "Unable to create card");
            cards.add(card);
        }
        assertTrue(userDeckService.delete(deck), "Unable to delete deck");

        // when: retrieving all cards for that user and deck
        List<Card> loadedCards = cardService.getAllCards(deck, person);

        // then: no cards must be loaded
        assertEquals(0, loadedCards.size(), "Got wrong number of cards");
    }

    @Test
    public void testGetCardsFromSubscribedPublishedDeck() {
        // given: a user, that subscribed to a deck with multiple cards
        int numCardsPerDeck = 10;
        Person person = createUser("person-testGetCardsFromDeckSubscribedPublished");
        Deck deck = createDeck("deck-testGetCardsFromDeckSubscribedPublished", createUser("person-testGetCardsFromDeckSubscribedPublished-creator"));
        List<Card> cards = new ArrayList<>();
        for (int i = 0; i < numCardsPerDeck; i++) {
            Card card = new Card(StringGenerator.cardText(), StringGenerator.cardText(),false, deck);
            assertTrue(cardService.create(card), "Unable to create card");
            cards.add(card);
        }
        assertTrue(userDeckService.publish(deck), "Unable to publish deck");
        assertTrue(userDeckService.subscribe(deck, person), "Unable to subscribe to deck");

        // when: retrieving all cards for that user and deck
        List<Card> loadedCards = cardService.getAllCards(deck, person);

        // then: all cards must be loaded again
        assertEquals(cards.size(), loadedCards.size(), "Got wrong number of cards");
        for (Card card : cards) {
            assertTrue(loadedCards.contains(card), "Unable to find a card");
        }
    }

    @Test
    public void testGetCardsFromSubscribedUnpublishedDeck() {
        // given: a user, that subscribed to a deck with multiple cards, but the deck was unpublished after subscription
        int numCardsPerDeck = 10;
        Person person = createUser("person-testGetCardsFromDeckSubscribedUnpublished");
        Deck deck = createDeck("deck-testGetCardsFromDeckSubscribedUnpublished", createUser("person-testGetCardsFromDeckSubscribedUnpublished-creator"));
        List<Card> cards = new ArrayList<>();
        for (int i = 0; i < numCardsPerDeck; i++) {
            Card card = new Card(StringGenerator.cardText(), StringGenerator.cardText(),false, deck);
            assertTrue(cardService.create(card), "Unable to create card");
            cards.add(card);
        }
        assertTrue(userDeckService.publish(deck), "Unable to publish deck");
        assertTrue(userDeckService.subscribe(deck, person), "Unable to subscribe to deck");
        assertTrue(userDeckService.unpublish(deck), "Unable to unpublish deck");

        // when: retrieving all cards for that user and deck
        List<Card> loadedCards = cardService.getAllCards(deck, person);

        // then: no cards must be loaded
        assertEquals(0, loadedCards.size(), "Got wrong number of cards");
    }

    @Test
    public void testGetCardsFromSubscribedBlockedDeck() {
        // given: a user, that subscribed to a deck with multiple cards, but the deck was blocked after subscription
        int numCardsPerDeck = 10;
        Person person = createUser("person-testGetCardsFromDeckSubscribedBlocked");
        Deck deck = createDeck("deck-testGetCardsFromDeckSubscribedBlocked", createUser("person-testGetCardsFromDeckSubscribedBlocked-creator"));
        List<Card> cards = new ArrayList<>();
        for (int i = 0; i < numCardsPerDeck; i++) {
            Card card = new Card(StringGenerator.cardText(), StringGenerator.cardText(),false, deck);
            assertTrue(cardService.create(card), "Unable to create card");
            cards.add(card);
        }
        assertTrue(userDeckService.publish(deck), "Unable to publish deck");
        assertTrue(userDeckService.subscribe(deck, person), "Unable to subscribe to deck");
        assertTrue(adminDeckService.block(deck), "Unable to block deck");

        // when: retrieving all cards for that user and deck
        List<Card> loadedCards = cardService.getAllCards(deck, person);

        // then: no cards must be loaded
        assertEquals(0, loadedCards.size(), "Got wrong number of cards");
    }

    @Test
    public void testGetCardsFromSubscribedDeletedDeck() {
        // given: a user, that subscribed to a deck with multiple cards, but the deck was blocked after subscription
        int numCardsPerDeck = 10;
        Person person = createUser("person-testGetCardsFromDeckSubscribedDeleted");
        Deck deck = createDeck("deck-testGetCardsFromDeckSubscribedDeleted", createUser("person-testGetCardsFromDeckSubscribedDeleted-creator"));
        List<Card> cards = new ArrayList<>();
        for (int i = 0; i < numCardsPerDeck; i++) {
            Card card = new Card(StringGenerator.cardText(), StringGenerator.cardText(),false, deck);
            assertTrue(cardService.create(card), "Unable to create card");
            cards.add(card);
        }
        assertTrue(userDeckService.publish(deck), "Unable to publish deck");
        assertTrue(userDeckService.subscribe(deck, person), "Unable to subscribe to deck");
        assertTrue(userDeckService.delete(deck), "Unable to delete deck");

        // when: retrieving all cards for that user and deck
        List<Card> loadedCards = cardService.getAllCards(deck, person);

        // then: no cards must be loaded
        assertEquals(0, loadedCards.size(), "Got wrong number of cards");
    }

    @Test
    public void testGetDeletedCardsFromDeck() {
        // given: a user, that a deck with multiple cards and deleted a card after creation
        int numCardsPerDeck = 10;
        Person person = createUser("person-testGetDeletedCardsFromDeck");
        Deck deck = createDeck("deck-testGetDeletedCardsFromDeck", createUser("person-testGetDeletedCardsFromDeck-creator"));
        List<Card> cards = new ArrayList<>();
        for (int i = 0; i < numCardsPerDeck; i++) {
            Card card = new Card(StringGenerator.cardText(), StringGenerator.cardText(),false, deck);
            assertTrue(cardService.create(card), "Unable to create card");
            cards.add(card);
        }
        assertTrue(userDeckService.publish(deck), "Unable to publish deck");
        assertTrue(userDeckService.subscribe(deck, person), "Unable to subscribe to deck");
        assertTrue(cardService.delete(cards.get(0)), "Unable to delete card");
        cards.remove(0);

        // when: retrieving all cards for that user and deck
        List<Card> loadedCards = cardService.getAllCards(deck, person);

        // then: all cards, except the deleted card must be loaded
        assertEquals(cards.size(), loadedCards.size(), "Got wrong number of cards");
        for (Card card : cards) {
            assertTrue(loadedCards.contains(card), "Unable to find card");
        }
    }

    @Test
    public void testGetCardsFromUnpublishedDeckAsAdmin() {
        // given: an admin and an unpublished deck created by a user
        int numCardsPerDeck = 10;
        Person admin = createAdmin("person-testGetCardsFromUnpublishedDeckAsAdmin-admin");
        Deck deck = createDeck("deck-testGetCardsFromUnpublishedDeckAsAdmin", createUser("person-testGetCardsFromUnpublishedDeckAsAdmin-creator"));
        List<Card> cards = new ArrayList<>();
        for (int i = 0; i < numCardsPerDeck; i++) {
            Card card = new Card(StringGenerator.cardText(), StringGenerator.cardText(),false, deck);
            assertTrue(cardService.create(card), "Unable to create card");
            cards.add(card);
        }

        // when: retrieving all cards for the admin and deck
        List<Card> loadedCards = cardService.getAllCards(deck, admin);

        // then: all cards must be loaded
        assertEquals(cards.size(), loadedCards.size(), "Got wrong number of cards");
        for (Card card : cards) {
            assertTrue(loadedCards.contains(card), "Unable to find card");
        }
    }

    @Test
    public void testGetCardsFromPublishedDeckAsAdmin() {
        // given: an admin and an published deck created by a user
        int numCardsPerDeck = 10;
        Person admin = createAdmin("person-testGetCardsFromPublishedDeckAsAdmin-admin");
        Deck deck = createDeck("deck-testGetCardsFromPublishedDeckAsAdmin", createUser("person-testGetCardsFromPublishedDeckAsAdmin-creator"));
        List<Card> cards = new ArrayList<>();
        for (int i = 0; i < numCardsPerDeck; i++) {
            Card card = new Card(StringGenerator.cardText(), StringGenerator.cardText(),false, deck);
            assertTrue(cardService.create(card), "Unable to create card");
            cards.add(card);
        }
        assertTrue(userDeckService.publish(deck), "Unable to publish deck");

        // when: retrieving all cards for the admin and deck
        List<Card> loadedCards = cardService.getAllCards(deck, admin);

        // then: all cards must be loaded
        assertEquals(cards.size(), loadedCards.size(), "Got wrong number of cards");
        for (Card card : cards) {
            assertTrue(loadedCards.contains(card), "Unable to find card");
        }
    }

    @Test
    public void testGetCardsFromBlockedDeckAsAdmin() {
        // given: an admin and a blocked deck created by a user
        int numCardsPerDeck = 10;
        Person admin = createAdmin("person-testGetCardsFromBlockedDeckAsAdmin-admin");
        Deck deck = createDeck("deck-testGetCardsFromBlockedDeckAsAdmin", createUser("person-testGetCardsFromBlockedDeckAsAdmin-creator"));
        List<Card> cards = new ArrayList<>();
        for (int i = 0; i < numCardsPerDeck; i++) {
            Card card = new Card(StringGenerator.cardText(), StringGenerator.cardText(),false, deck);
            assertTrue(cardService.create(card), "Unable to create card");
            cards.add(card);
        }
        assertTrue(adminDeckService.block(deck), "Unable to block deck");

        // when: retrieving all cards for the admin and deck
        List<Card> loadedCards = cardService.getAllCards(deck, admin);

        // then: all cards must be loaded
        assertEquals(cards.size(), loadedCards.size(), "Got wrong number of cards");
        for (Card card : cards) {
            assertTrue(loadedCards.contains(card), "Unable to find card");
        }
    }

    @Test
    public void testGetCardsFromDeletedDeckAsAdmin() {
        // given: an admin and a deleted deck created by a user
        int numCardsPerDeck = 10;
        Person admin = createAdmin("person-testGetCardsFromDeletedDeckAsAdmin-admin");
        Deck deck = createDeck("deck-testGetCardsFromDeletedDeckAsAdmin", createUser("person-testGetCardsFromDeletedDeckAsAdmin-creator"));
        List<Card> cards = new ArrayList<>();
        for (int i = 0; i < numCardsPerDeck; i++) {
            Card card = new Card(StringGenerator.cardText(), StringGenerator.cardText(),false, deck);
            assertTrue(cardService.create(card), "Unable to create card");
            cards.add(card);
        }
        assertTrue(userDeckService.delete(deck), "Unable to delete deck");

        // when: retrieving all cards for that user and deck
        List<Card> loadedCards = cardService.getAllCards(deck, admin);

        // then: no cards must be loaded
        assertEquals(0, loadedCards.size(), "Got wrong number of cards");
    }

    @Test
    public void testGetDeletedCardsFromDeckAsAdmin() {
        // given: an admin and a deck created by a user, where a card was deleted
        int numCardsPerDeck = 10;
        Person admin = createAdmin("person-testGetDeletedCardsFromDeckAsAdmin-admin");
        Deck deck = createDeck("deck-testGetDeletedCardsFromDeckAsAdmin", createUser("person-testGetDeletedCardsFromDeckAsAdmin-creator"));
        List<Card> cards = new ArrayList<>();
        for (int i = 0; i < numCardsPerDeck; i++) {
            Card card = new Card(StringGenerator.cardText(), StringGenerator.cardText(),false, deck);
            assertTrue(cardService.create(card), "Unable to create card");
            cards.add(card);
        }
        assertTrue(cardService.delete(cards.get(0)), "Unable to delete card");
        cards.remove(0);

        // when: retrieving all cards for the admin and deck
        List<Card> loadedCards = cardService.getAllCards(deck, admin);

        // then: all cards, except the deleted card must be loaded
        assertEquals(cards.size(), loadedCards.size(), "Got wrong number of cards");
        for (Card card : cards) {
            assertTrue(loadedCards.contains(card), "Unable to find card");
        }
    }
}
