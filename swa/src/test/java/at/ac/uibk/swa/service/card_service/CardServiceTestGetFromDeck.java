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
public class CardServiceTestGetFromDeck {
    @Autowired
    private UserDeckService userDeckService;
    @Autowired
    private AdminDeckService adminDeckService;
    @Autowired
    private CardService cardService;
    @Autowired
    private PersonService personService;

    private Person createUserAndLogin(String username) {
        Person person = new Person(username, StringGenerator.email(), StringGenerator.password(), Set.of(Permission.USER));
        assertTrue(personService.create(person), "Unable to create user");
        return (Person) MockAuthContext.setLoggedInUser(person);
    }

    private Person createAdminAndLogin(String username) {
        Person person = new Person(username, StringGenerator.email(), StringGenerator.password(), Set.of(Permission.ADMIN));
        assertTrue(personService.create(person), "Unable to create user");
        return (Person) MockAuthContext.setLoggedInUser(person);
    }

    private Deck createDeck(String name) {
        Deck deck = new Deck(name, StringGenerator.deckDescription());
        assertTrue(userDeckService.create(deck), "Unable to create deck");
        return deck;
    }

    @Test
    public void testGetCardsFromOwnedDeck() {
        // given: a user, that created a deck with multiple cards
        int numCardsPerDeck = 10;
        Person person = createUserAndLogin("person-testGetCardsFromDeckOwned");
        Deck deck = createDeck("deck-testGetCardsFromDeckOwned");
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
    public void testGetCardsFromOwnedBlockedDeck() {
        // given: a user, that created a deck with multiple cards, where the deck got blocked after creation
        int numCardsPerDeck = 10;
        Person person = createUserAndLogin("person-testGetCardsFromDeckOwnedBlocked");
        Deck deck = createDeck("deck-testGetCardsFromDeckOwnedBlocked");
        List<Card> cards = new ArrayList<>();
        for (int i = 0; i < numCardsPerDeck; i++) {
            Card card = new Card(StringGenerator.cardText(), StringGenerator.cardText(),false);
            assertTrue(cardService.create(card, deck.getDeckId()), "Unable to create card");
            cards.add(card);
        }
        assertTrue(adminDeckService.block(deck), "Unable to block deck");

        // when: retrieving all cards for that user and deck
        Optional<List<Card>> maybeLoadedCards = cardService.getAllCards(deck.getDeckId());

        // then: no cards must be loaded
        assertTrue(maybeLoadedCards.isPresent(), "Unable to load cards");
        List<Card> loadedCards = maybeLoadedCards.get();
        assertEquals(0, loadedCards.size(), "Got wrong number of cards");
    }

    @Test
    public void testGetCardsFromOwnedDeletedDeck() {
        // given: a user, that created a deck with multiple cards, where the deck got deleted after creation
        int numCardsPerDeck = 10;
        Person person = createUserAndLogin("person-testGetCardsFromDeckOwnedDeleted");
        Deck deck = createDeck("deck-testGetCardsFromDeckOwnedDeleted");
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
    public void testGetCardsFromSubscribedPublishedDeck() {
        // given: a user, that subscribed to a deck with multiple cards
        int numCardsPerDeck = 10;
        Person creator = createUserAndLogin("person-testGetCardsFromDeckSubscribedPublished-creator");
        Deck deck = createDeck("deck-testGetCardsFromDeckSubscribedPublished");
        List<Card> cards = new ArrayList<>();
        for (int i = 0; i < numCardsPerDeck; i++) {
            Card card = new Card(StringGenerator.cardText(), StringGenerator.cardText(),false);
            assertTrue(cardService.create(card, deck.getDeckId()), "Unable to create card");
            cards.add(card);
        }
        assertTrue(userDeckService.publish(deck.getDeckId()), "Unable to publish deck");
        Person person = createUserAndLogin("person-testGetCardsFromDeckSubscribedPublished");
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
    public void testGetCardsFromSubscribedUnpublishedDeck() {
        // given: a user, that subscribed to a deck with multiple cards, but the deck was unpublished after subscription
        int numCardsPerDeck = 10;
        Person creator = createUserAndLogin("person-testGetCardsFromDeckSubscribedUnpublished-creator");
        Deck deck = createDeck("deck-testGetCardsFromDeckSubscribedUnpublished");
        List<Card> cards = new ArrayList<>();
        for (int i = 0; i < numCardsPerDeck; i++) {
            Card card = new Card(StringGenerator.cardText(), StringGenerator.cardText(),false);
            assertTrue(cardService.create(card, deck.getDeckId()), "Unable to create card");
            cards.add(card);
        }
        assertTrue(userDeckService.publish(deck.getDeckId()), "Unable to publish deck");
        Person person = createUserAndLogin("person-testGetCardsFromDeckSubscribedUnpublished");
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
    public void testGetCardsFromSubscribedBlockedDeck() {
        // given: a user, that subscribed to a deck with multiple cards, but the deck was blocked after subscription
        int numCardsPerDeck = 10;
        Person creator = createUserAndLogin("person-testGetCardsFromDeckSubscribedBlocked-creator");
        Deck deck = createDeck("deck-testGetCardsFromDeckSubscribedBlocked");
        List<Card> cards = new ArrayList<>();
        for (int i = 0; i < numCardsPerDeck; i++) {
            Card card = new Card(StringGenerator.cardText(), StringGenerator.cardText(),false);
            assertTrue(cardService.create(card, deck.getDeckId()), "Unable to create card");
            cards.add(card);
        }
        assertTrue(userDeckService.publish(deck.getDeckId()), "Unable to publish deck");
        Person person = createUserAndLogin("person-testGetCardsFromDeckSubscribedBlocked");
        assertTrue(userDeckService.subscribe(deck.getDeckId()), "Unable to subscribe to deck");
        assertTrue(adminDeckService.block(deck), "Unable to block deck");

        // when: retrieving all cards for that user and deck
        Optional<List<Card>> maybeLoadedCards = cardService.getAllCards(deck.getDeckId());

        // then: no cards must be loaded
        assertTrue(maybeLoadedCards.isPresent(), "Unable to load cards");
        List<Card> loadedCards = maybeLoadedCards.get();
        assertEquals(0, loadedCards.size(), "Got wrong number of cards");
    }

    @Test
    public void testGetCardsFromSubscribedDeletedDeck() {
        // given: a user, that subscribed to a deck with multiple cards, but the deck was blocked after subscription
        int numCardsPerDeck = 10;
        Person creator = createUserAndLogin("person-testGetCardsFromDeckSubscribedDeleted-creator");
        Deck deck = createDeck("deck-testGetCardsFromDeckSubscribedDeleted");
        List<Card> cards = new ArrayList<>();
        for (int i = 0; i < numCardsPerDeck; i++) {
            Card card = new Card(StringGenerator.cardText(), StringGenerator.cardText(),false);
            assertTrue(cardService.create(card, deck.getDeckId()), "Unable to create card");
            cards.add(card);
        }
        assertTrue(userDeckService.publish(deck.getDeckId()), "Unable to publish deck");
        Person person = createUserAndLogin("person-testGetCardsFromDeckSubscribedDeleted");
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
    public void testGetDeletedCardsFromDeck() {
        // given: a user, that a deck with multiple cards and deleted a card after creation
        int numCardsPerDeck = 10;
        Person creator = createUserAndLogin("person-testGetDeletedCardsFromDeck-creator");
        Deck deck = createDeck("deck-testGetDeletedCardsFromDeck");
        List<Card> cards = new ArrayList<>();
        for (int i = 0; i < numCardsPerDeck; i++) {
            Card card = new Card(StringGenerator.cardText(), StringGenerator.cardText(),false);
            assertTrue(cardService.create(card, deck.getDeckId()), "Unable to create card");
            cards.add(card);
        }
        assertTrue(userDeckService.publish(deck.getDeckId()), "Unable to publish deck");
        Person person = createUserAndLogin("person-testGetDeletedCardsFromDeck");
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
    public void testGetCardsFromUnpublishedDeckAsAdmin() {
        // given: an admin and an unpublished deck created by a user
        int numCardsPerDeck = 10;
        Person creator = createUserAndLogin("person-testGetCardsFromUnpublishedDeckAsAdmin-creator");
        Deck deck = createDeck("deck-testGetCardsFromUnpublishedDeckAsAdmin");
        List<Card> cards = new ArrayList<>();
        for (int i = 0; i < numCardsPerDeck; i++) {
            Card card = new Card(StringGenerator.cardText(), StringGenerator.cardText(),false);
            assertTrue(cardService.create(card, deck.getDeckId()), "Unable to create card");
            cards.add(card);
        }
        Person admin = createAdminAndLogin("person-testGetCardsFromUnpublishedDeckAsAdmin-admin");

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
    public void testGetCardsFromPublishedDeckAsAdmin() {
        // given: an admin and an published deck created by a user
        int numCardsPerDeck = 10;
        Person creator = createUserAndLogin("person-testGetCardsFromPublishedDeckAsAdmin-creator");
        Deck deck = createDeck("deck-testGetCardsFromPublishedDeckAsAdmin");
        List<Card> cards = new ArrayList<>();
        for (int i = 0; i < numCardsPerDeck; i++) {
            Card card = new Card(StringGenerator.cardText(), StringGenerator.cardText(),false);
            assertTrue(cardService.create(card, deck.getDeckId()), "Unable to create card");
            cards.add(card);
        }
        assertTrue(userDeckService.publish(deck.getDeckId()), "Unable to publish deck");
        Person admin = createAdminAndLogin("person-testGetCardsFromPublishedDeckAsAdmin-admin");

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
    public void testGetCardsFromBlockedDeckAsAdmin() {
        // given: an admin and a blocked deck created by a user
        int numCardsPerDeck = 10;
        Person creator = createUserAndLogin("person-testGetCardsFromBlockedDeckAsAdmin-creator");
        Deck deck = createDeck("deck-testGetCardsFromBlockedDeckAsAdmin");
        List<Card> cards = new ArrayList<>();
        for (int i = 0; i < numCardsPerDeck; i++) {
            Card card = new Card(StringGenerator.cardText(), StringGenerator.cardText(),false);
            assertTrue(cardService.create(card, deck.getDeckId()), "Unable to create card");
            cards.add(card);
        }
        Person admin = createAdminAndLogin("person-testGetCardsFromBlockedDeckAsAdmin-admin");
        assertTrue(adminDeckService.block(deck), "Unable to block deck");

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
    public void testGetCardsFromDeletedDeckAsAdmin() {
        // given: an admin and a deleted deck created by a user
        int numCardsPerDeck = 10;
        Person creator = createUserAndLogin("person-testGetCardsFromDeletedDeckAsAdmin-creator");
        Deck deck = createDeck("deck-testGetCardsFromDeletedDeckAsAdmin");
        List<Card> cards = new ArrayList<>();
        for (int i = 0; i < numCardsPerDeck; i++) {
            Card card = new Card(StringGenerator.cardText(), StringGenerator.cardText(),false);
            assertTrue(cardService.create(card, deck.getDeckId()), "Unable to create card");
            cards.add(card);
        }
        assertTrue(userDeckService.delete(deck.getDeckId()), "Unable to delete deck");
        Person admin = createAdminAndLogin("person-testGetCardsFromDeletedDeckAsAdmin-admin");

        // when: retrieving all cards for that user and deck
        Optional<List<Card>> maybeLoadedCards = cardService.getAllCards(deck.getDeckId());

        // then: no cards must be loaded
        assertTrue(maybeLoadedCards.isPresent(), "Unable to load cards");
        List<Card> loadedCards = maybeLoadedCards.get();
        assertEquals(0, loadedCards.size(), "Got wrong number of cards");
    }

    @Test
    public void testGetDeletedCardsFromDeckAsAdmin() {
        // given: an admin and a deck created by a user, where a card was deleted
        int numCardsPerDeck = 10;
        Person creator = createUserAndLogin("person-testGetDeletedCardsFromDeckAsAdmin-creator");
        Deck deck = createDeck("deck-testGetDeletedCardsFromDeckAsAdmin");
        List<Card> cards = new ArrayList<>();
        for (int i = 0; i < numCardsPerDeck; i++) {
            Card card = new Card(StringGenerator.cardText(), StringGenerator.cardText(),false);
            assertTrue(cardService.create(card, deck.getDeckId()), "Unable to create card");
            cards.add(card);
        }
        assertTrue(cardService.delete(cards.get(0).getCardId()), "Unable to delete card");
        cards.remove(0);
        Person admin = createAdminAndLogin("person-testGetDeletedCardsFromDeckAsAdmin-admin");

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
}
