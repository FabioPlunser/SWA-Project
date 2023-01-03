package at.ac.uibk.swa.service;

import at.ac.uibk.swa.config.person_authentication.AuthContext;
import at.ac.uibk.swa.models.Authenticable;
import at.ac.uibk.swa.models.Deck;
import at.ac.uibk.swa.models.Person;
import at.ac.uibk.swa.repositories.DeckRepository;
import at.ac.uibk.swa.repositories.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;

@Service("userDeckService")
public class UserDeckService {
    @Autowired
    DeckRepository deckRepository;
    @Autowired
    PersonRepository personRepository;

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
     * Finds all decks in the repository that are public and available for subscription (not deleted/blocked)
     * Might return decks that are already subscribed TODO: Change that? Person as parameter would be required
     *
     *
     * @return list of all available decks
     */
    public List<Deck> findAllAvailableDecks() {
        Optional<Authenticable> maybeUser = AuthContext.getCurrentUser();
        return deckRepository.findAll().stream()
                .filter(d -> d.isPublished() || (maybeUser.isPresent() && d.getCreator().equals(maybeUser.get())))
                .filter(Predicate.not(Deck::isBlocked))
                .filter(Predicate.not(Deck::isDeleted))
                .toList();
    }

    /**
     * Gets all decks to which the currently logged in user has subscribed, but might alter description, depending on
     * deck status
     *  - isDeleted: info, that deck has been deleted
     *  - isBlocked: info, that deck has been blocked
     *  - !isPublished: info, that deck has been unpublished, if not creator
     *
     * @return a list of all decks to which that person has subscribed or nothing if nobody is logged in
     */
    public Optional<List<Deck>> getAllSavedDecks() {
        Optional<Authenticable> maybeUser = AuthContext.getCurrentUser();
        if (maybeUser.isPresent() && maybeUser.get() instanceof Person person) {
            return Optional.of(person.getSavedDecks().stream()
                    .map(d -> {if (!d.getCreator().equals(person) && !d.isPublished()) { d.setDescription("Deck has been unpublished"); } return d;})
                    .map(d -> {if (d.isBlocked()) { d.setDescription("Deck has been blocked"); } return d;})
                    .map(d -> {if (d.isDeleted()) { d.setDescription("Deck has been deleted"); } return d;})
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
        Optional<Authenticable> maybeUser = AuthContext.getCurrentUser();
        if (maybeUser.isPresent() && maybeUser.get() instanceof Person person) {
            return Optional.of(person.getCreatedDecks().stream()
                    .filter(Predicate.not(Deck::isDeleted))
                    .map(d -> {if (d.isBlocked()) { d.setDescription("Deck has been blocked"); } return d;})
                    .toList());
        } else {
            return Optional.empty();
        }
    }

    /**
     * Gets all decks to which the currently logged-in user has subscribed to but did not create, but might alter description, depending on
     * deck status
     *  - isDeleted: info, that deck has been deleted
     *  - isBlocked: info, that deck has been blocked
     *  - !isPublished: info, that deck has been unpublished, if not creator
     *
     * @return a list of all decks to which that person has subscribed to (but did not create) or nothing if nobody is logged in
     */
    // TODO: Rename this Method to something more fitting
    public Optional<List<Deck>> getSavedNotOwnedDecks() {
        Optional<Authenticable> maybeUser = AuthContext.getCurrentUser();
        if (maybeUser.isPresent() && maybeUser.get() instanceof Person person) {
            return getAllSavedDecks().map(decks -> decks.stream().filter(Predicate.not(d -> d.isCreator(person))).toList());
        }
        return Optional.empty();
    }

    /**
     * Saves a deck to the repository
     *
     * @param deck deck to save
     * @return deck that has been saved if successfull, null otherwise
     */
    private Deck save(Deck deck) {
        try {
            return deckRepository.save(deck);
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
    public boolean create(Deck deck) {
        Optional<Authenticable> maybeUser = AuthContext.getCurrentUser();
        if (deck != null && deck.getDeckId() == null && maybeUser.isPresent() && maybeUser.get() instanceof Person person) {
            deck.setCreator(person);
            Deck savedDeck = save(deck);
            if (savedDeck != null) {
                person.getCreatedDecks().add(savedDeck);
                person.getSavedDecks().add(savedDeck);
                try {
                    personRepository.save(person);
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
    }

    /**
     * Updates one of the owned decks of the logged in user in the repository with the given parameters
     * Deleted and blocked decks cannot be updated
     *
     * @param deckId id of the deck that is to be updated
     * @param name new name of the deck, set to null if no change is desired
     * @param description new description of the deck, set to null if no change is desired
     * @return true if the deck was updated, false otherwise
     */
    public boolean update(UUID deckId, String name, String description) {
        Optional<Authenticable> maybeUser = AuthContext.getCurrentUser();
        if (maybeUser.isPresent() && maybeUser.get() instanceof Person person) {
            Deck deck = person.getCreatedDecks().stream().filter(d -> d.getDeckId().equals(deckId)).findFirst().orElse(null);
            if (deck != null && deck.getDeckId() != null) {
                if (deck.isBlocked() || deck.isDeleted()) return false;
                if (name != null) deck.setName(name);
                if (description != null) deck.setDescription(description);
                return save(deck) != null;
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
    public boolean delete(UUID deckId) {
        Optional<Authenticable> maybeUser = AuthContext.getCurrentUser();
        if (maybeUser.isPresent() && maybeUser.get() instanceof Person person) {
            Deck deck = person.getCreatedDecks().stream().filter(d -> d.getDeckId().equals(deckId)).findFirst().orElse(null);
            if (deck != null && deck.getDeckId() != null) {
                if (deck.isDeleted()) return false;
                deck.setDeleted(true);
                Deck savedDeck = save(deck);
                if (savedDeck != null) {
                    person.getSavedDecks().remove(savedDeck);
                    try {
                        personRepository.save(person);
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
    public boolean publish(UUID deckId) {
        Optional<Authenticable> maybeUser = AuthContext.getCurrentUser();
        if (maybeUser.isPresent() && maybeUser.get() instanceof Person person) {
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
    public boolean unpublish(UUID deckId) {
        Optional<Authenticable> maybeUser = AuthContext.getCurrentUser();
        if (maybeUser.isPresent() && maybeUser.get() instanceof Person person) {
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
    public boolean subscribe(UUID deckId) {
        Optional<Authenticable> maybeUser = AuthContext.getCurrentUser();
        if (maybeUser.isPresent() && maybeUser.get() instanceof Person person) {
            Deck deck = findAllAvailableDecks().stream().filter(d -> d.getDeckId().equals(deckId)).findFirst().orElse(null);
            if (deck != null && deck.getDeckId() != null && person.getPersonId() != null) {
                if (!person.getSavedDecks().contains(deck)) {
                    person.getSavedDecks().add(deck);
                    try {
                        Person savedPerson = personRepository.save(person);
                        savedPerson.getSavedDecks().get(savedPerson.getSavedDecks().indexOf(deck)).getSubscribedPersons().add(savedPerson);
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
    public boolean unsubscribe(UUID deckId) {
        Optional<Authenticable> maybeUser = AuthContext.getCurrentUser();
        if (maybeUser.isPresent() && maybeUser.get() instanceof Person person) {
            Deck deck = person.getSavedDecks().stream().filter(d -> d.getDeckId().equals(deckId)).findFirst().orElse(null);
            if (deck != null && deck.getDeckId() != null && person.getPersonId() != null) {
                if (person.getSavedDecks().contains(deck)) {
                    person.getSavedDecks().remove(deck);
                    try {
                        Person savedPerson = personRepository.save(person);
                        savedPerson.getSavedDecks().get(savedPerson.getSavedDecks().indexOf(deck)).getSubscribedPersons().remove(savedPerson);
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
}
