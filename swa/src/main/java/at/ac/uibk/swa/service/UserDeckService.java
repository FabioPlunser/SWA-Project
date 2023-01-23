package at.ac.uibk.swa.service;

import at.ac.uibk.swa.config.jwt_authentication.AuthContext;
import at.ac.uibk.swa.models.Card;
import at.ac.uibk.swa.models.Deck;
import at.ac.uibk.swa.models.Person;
import at.ac.uibk.swa.repositories.CardRepository;
import at.ac.uibk.swa.repositories.DeckRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Stream;

@Service("userDeckService")
public class UserDeckService {
    @Autowired
    DeckRepository deckRepository;
    @Autowired
    PersonService personService;
    @Autowired
    CardRepository cardRepository;

    private static final String DECK_UNPUBLISHED_INFO = "Deck has been unpublished";
    private static final String DECK_BLOCKED_INFO = "Deck has been blocked";
    private static final String DECK_DELETED_INFO = "Deck has been deleted";

    /**
     * Finds a deck within the repository by its id
     *
     * @param id id of the deck to be return
     * @return deck with given id (if found), otherwise nothing
     */
    public Optional<Deck> findById(UUID id) {
        return deckRepository.findById(id);
    }

    /**
     * Finds all decks in the repository that are public and available for subscription (not deleted/blocked/already subscribed)
     *
     * @return list of all available decks
     */
    public List<Deck> findAllAvailableDecks() {
        Optional<Person> maybePerson = AuthContext.getCurrentPerson();
        return deckRepository.findAll().stream()
                .filter(Predicate.not(Deck::isDeleted))
                .filter(Predicate.not(Deck::isBlocked))
                .filter(Deck::isPublished)
                .filter(d -> maybePerson.isEmpty() || !maybePerson.get().getSavedDecks().contains(d))
                .toList();
    }

    /**
     * Gets all decks to which the currently logged in user has subscribed, but might alter description, depending on
     * deck status
     * - isDeleted: info, that deck has been deleted
     * - isBlocked: info, that deck has been blocked
     * - !isPublished: info, that deck has been unpublished, if not creator
     *
     * @return a list of all decks to which that person has subscribed or nothing if nobody is logged in
     */
    public Optional<List<Deck>> getAllViewableDecks() {
        Optional<Person> maybePerson = AuthContext.getCurrentPerson();
        if (maybePerson.isPresent()) {
            Person person = maybePerson.get();
            return Optional.of(person.getSavedDecks().stream()
                    .map(d -> {
                        if (!d.getCreator().equals(person) && !d.isPublished()) {
                            d.setDescription(DECK_UNPUBLISHED_INFO);
                        }
                        return d;
                    })
                    .map(d -> {
                        if (d.isBlocked()) {
                            d.setDescription(DECK_BLOCKED_INFO);
                        }
                        return d;
                    })
                    .map(d -> {
                        if (d.isDeleted()) {
                            d.setDescription(DECK_DELETED_INFO);
                        }
                        return d;
                    })
                    .toList());
        } else {
            return Optional.empty();
        }
    }

    /**
     * Gets all decks owned by the logged in user from the repository
     *
     * @return list of owned decks or nothing if nobody is logged in
     */
    public Optional<List<Deck>> getAllOwnedDecks() {
        Optional<Person> maybePerson = AuthContext.getCurrentPerson();
        if (maybePerson.isPresent()) {
            Person person = maybePerson.get();
            return Optional.of(person.getCreatedDecks().stream()
                    .filter(Predicate.not(Deck::isDeleted))
                    .map(d -> {
                        if (d.isBlocked()) {
                            d.setDescription(DECK_BLOCKED_INFO);
                        }
                        return d;
                    })
                    .toList());
        } else {
            return Optional.empty();
        }
    }

    /**
     * Gets all decks to which the currently logged-in user has subscribed to but did not create, but might alter description, depending on
     * deck status
     * - isDeleted: info, that deck has been deleted
     * - isBlocked: info, that deck has been blocked
     * - !isPublished: info, that deck has been unpublished, if not creator
     *
     * @return a list of all decks to which that person has subscribed to (but did not create) or nothing if nobody is logged in
     */
    public Optional<List<Deck>> getAllSubscribedDecks() {
        Optional<Person> maybePerson = AuthContext.getCurrentPerson();
        if (maybePerson.isPresent()) {
            Person person = maybePerson.get();
            return getAllViewableDecks().map(decks -> decks.stream().filter(Predicate.not(d -> d.isCreator(person))).toList());
        }
        return Optional.empty();
    }

    /**
     * Gets all decks a given user has created
     *
     * @param personId
     * @return
     */
    public Optional<List<Deck>> getDecksOfGivenPerson(UUID personId) {
        Optional<Person> maybeUser = personService.findById(personId);
        if (maybeUser.isPresent()) {
            Person person = maybeUser.get();
            return Optional.of(person.getCreatedDecks().stream()
                    .filter(Predicate.not(Deck::isDeleted))
                    .map(d -> {
                        if (d.isBlocked()) {
                            d.setDescription(DECK_BLOCKED_INFO);
                        }
                        return d;
                    })
                    .toList());
        }
        return Optional.empty();
    }

    /**
     * Saves a deck to the repository
     *
     * @param deck deck to save
     * @return deck that has been saved if successful, null otherwise
     */
    @Transactional
    public Deck save(Deck deck) {
        try {
            Deck savedDeck = deckRepository.save(deck);
            for (Card card : deck.getCards()) {
                card.setDeck(savedDeck);
                cardRepository.save(card);
            }
            return savedDeck;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Creates a new deck in the repository owned by the currently logged in user
     *
     * @param deck deck to be created
     * @return true if deck has been created, false otherwise
     */
    @Transactional
    public boolean create(Deck deck) {
        Optional<Person> maybePerson = AuthContext.getCurrentPerson();
        if (deck != null && deck.getDeckId() == null && maybePerson.isPresent()) {
            Person person = maybePerson.get();
            deck.setCreator(person);
            deck.addSubscriber(person);
            person.getCreatedDecks().add(deck);
            person.getSavedDecks().add(deck);
            return save(deck) != null;
        } else {
            return false;
        }
    }

    /**
     * Updates one of the owned decks of the logged in user in the repository with the given parameters
     * Deleted and blocked decks cannot be updated
     * Will change name, description and publicity of deck if given
     * Will also update/create/delete cards in the given deck if updateCards is set
     * - cards with given id:  update, if part of the deck, ignore otherwise
     * - cards without id:     create
     * - deletes all cards from the deck, that are not given
     *
     * @param deck        deck to be updated -  at least deckId must be given
     * @param updateCards true if cards should be update, false otherwise
     * @return true if the deck was updated, false otherwise
     */
    @Transactional
    public boolean update(Deck deck, boolean updateCards) {
        Optional<Person> maybePerson = AuthContext.getCurrentPerson();
        if (maybePerson.isPresent()) {
            Person person = maybePerson.get();
            Deck savedDeck = person.getCreatedDecks().stream().filter(d -> d.getDeckId().equals(deck.getDeckId())).findFirst().orElse(null);
            if (savedDeck != null) {
                if (savedDeck.isBlocked() || savedDeck.isDeleted()) return false;
                if (deck.getName() != null) savedDeck.setName(deck.getName());
                if (deck.getDescription() != null) savedDeck.setDescription(deck.getDescription());
                savedDeck.setPublished(deck.isPublished());
                if (updateCards) {
                    List<Card> cardsToUpdate = savedDeck.getCards().stream()
                            .filter(c -> deck.getCards().contains(c))
                            .map(c -> c.updateAllExceptLearningProgresses(deck.getCards().get(deck.getCards().indexOf(c))))
                            .toList();
                    List<Card> cardsToDelete = savedDeck.getCards().stream()
                            .filter(c -> !deck.getCards().contains(c))
                            .toList();
                    List<Card> cardsToCreate = deck.getCards().stream()
                            .filter(c -> c.getCardId() == null)
                            .toList();
                    savedDeck.setCards(Stream.concat(cardsToUpdate.stream(), cardsToCreate.stream()).toList());
                    for (Card card : cardsToDelete) {
                        try {
                            cardRepository.delete(card);
                        } catch (Exception e) {
                            return false;
                        }
                    }
                }
                return save(savedDeck) != null;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * Deletes one of the owned decks of the logged in user in the repository
     * Already deleted deck cannot be deleted again
     *
     * @param deckId id of the deck to be deleted
     * @return true if deck has been deleted, false otherwise
     */
    @Transactional
    public boolean delete(UUID deckId) {
        Optional<Person> maybePerson = AuthContext.getCurrentPerson();
        if (maybePerson.isPresent()) {
            Person person = maybePerson.get();
            Deck deck = person.getCreatedDecks().stream().filter(d -> d.getDeckId().equals(deckId)).findFirst().orElse(null);
            if (deck != null && deck.getDeckId() != null) {
                if (deck.isDeleted()) return false;
                deck.setDeleted(true);
                Deck savedDeck = save(deck);
                if (savedDeck != null) {
                    person.getSavedDecks().remove(savedDeck);
                    try {
                        personService.save(person);
                    } catch (Exception e) {
                        return false;
                    }
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * Publishes one of the decks owned by the logged in user in the repository
     * Already published deck cannot be published again
     *
     * @param deckId if of the deck to publish
     * @return true if deck has been published, false otherwise
     */
    @Transactional
    public boolean publish(UUID deckId) {
        Optional<Person> maybePerson = AuthContext.getCurrentPerson();
        if (maybePerson.isPresent()) {
            Person person = maybePerson.get();
            Deck deck = person.getCreatedDecks().stream().filter(d -> d.getDeckId().equals(deckId)).findFirst().orElse(null);
            if (deck != null && deck.getDeckId() != null) {
                if (deck.isPublished()) return false;
                deck.setPublished(true);
                return save(deck) != null;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * Unpublishes one of the decks owned by the logged in user in the repository
     * Already unpublished deck cannot be unpublished again
     *
     * @param deckId id of the deck to unpublish
     * @return true if deck has been unpublished, false otherwise
     */
    @Transactional
    public boolean unpublish(UUID deckId) {
        Optional<Person> maybePerson = AuthContext.getCurrentPerson();
        if (maybePerson.isPresent()) {
            Person person = maybePerson.get();
            Deck deck = person.getCreatedDecks().stream().filter(d -> d.getDeckId().equals(deckId)).findFirst().orElse(null);
            if (deck != null && deck.getDeckId() != null) {
                if (!deck.isPublished()) return false;
                deck.setPublished(false);
                return save(deck) != null;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * Subscribe the logged in user to a deck available for subscription
     * It is not possible to subscribe to a deck twice
     *
     * @param deckId id of the deck to subscribe to
     * @return true if the person has been subscribed, false otherwise
     */
    @Transactional
    public boolean subscribe(UUID deckId) {
        Optional<Person> maybePerson = AuthContext.getCurrentPerson();
        if (maybePerson.isPresent()) {
            Person person = maybePerson.get();
            Deck deck = findAllAvailableDecks().stream().filter(d -> d.getDeckId().equals(deckId)).findFirst().orElse(null);
            if (deck != null && deck.getDeckId() != null && person.getPersonId() != null) {
                if (!deck.getSubscribedPersons().contains(person)) {
                    deck.addSubscriber(person);
                    try {
                        deckRepository.save(deck);
                        person.getSavedDecks().add(deck);
                        return true;
                    } catch (Exception e) {
                        return false;
                    }
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * Unsubscribe the logged in person from one if their subscribed decks
     * The creator of a deck cannot unsubscribe from the deck
     *
     * @param deckId id of the deck to unsubscribe from
     * @return true if the person has been unsubscribed, false otherwise
     */
    @Transactional
    public boolean unsubscribe(UUID deckId) {
        Optional<Person> maybePerson = AuthContext.getCurrentPerson();
        if (maybePerson.isPresent()) {
            Person person = maybePerson.get();
            Deck deck = person.getSavedDecks().stream().filter(d -> d.getDeckId().equals(deckId)).findFirst().orElse(null);
            if (deck != null && deck.getDeckId() != null && person.getPersonId() != null) {
                if (person.getSavedDecks().contains(deck)) {
                    deck.removeSubscriber(person);
                    try {
                        deckRepository.save(deck);
                        person.getSavedDecks().remove(deck);
                        return true;
                    } catch (Exception e) {
                        return false;
                    }
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * Returns name of Deck as String, if Deck can be found by its ID
     *
     * @param deckId ID of Deck whose name is searched
     * @return deckName of searched Deck if Deck is present
     */
    public String getDeckNameIfPresent(UUID deckId) {
        String deckName = "";
        Optional<Deck> maybeDeck = findById(deckId);
        if (maybeDeck.isPresent()) {
            deckName = maybeDeck.get().getName();
        }
        return deckName;
    }
}
