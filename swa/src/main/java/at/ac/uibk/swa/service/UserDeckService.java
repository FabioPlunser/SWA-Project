package at.ac.uibk.swa.service;

import at.ac.uibk.swa.config.personAuthentication.AuthContext;
import at.ac.uibk.swa.models.Authenticable;
import at.ac.uibk.swa.models.Deck;
import at.ac.uibk.swa.models.Person;
import at.ac.uibk.swa.repositories.DeckRepository;
import at.ac.uibk.swa.repositories.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
        List<Deck> allDecks = deckRepository.findAll();
        return deckRepository.findAll().stream()
                .filter(Deck::isPublished)
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
     * @return a list of all decks to which that person has subscribed or nothing is nobody is logged in
     */
    public Optional<List<Deck>> getAllSavedDecks() {
        Optional<Authenticable> maybeUser = AuthContext.getCurrentUser();
        if (maybeUser.isPresent() && maybeUser.get() instanceof Person person) {
            return Optional.of(person.getSavedDecks().stream()
                    .map(d -> {if (!d.getCreator().equals(person) && !d.isPublished()) d.setDescription("Deck has been unpublished"); return d;})
                    .map(d -> {if (d.isBlocked()) d.setDescription("Deck has been blocked"); return d;})
                    .map(d -> {if (d.isDeleted()) d.setDescription("Deck has been deleted"); return d;})
                    .toList());
        } else {
            return Optional.empty();
        }
    }

    /**
     * Gets all decks owned by a specific user from the repository
     *
     * @param person person for which the owned decks should be found
     * @return list of owned decks, empty list if none have been found or person has not been found
     */
    public List<Deck> getAllOwnedDecks(Person person) {
        if (person != null && person.getPersonId() != null) {
            return person.getCreatedDecks().stream()
                    .filter(Predicate.not(Deck::isDeleted))
                    .map(d -> {if (d.isBlocked()) d.setDescription("Deck has been blocked"); return d;})
                    .toList();
        } else {
            return new ArrayList<>();
        }
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
                savedDeck.getCreator().getCreatedDecks().add(savedDeck);
                try {
                    personRepository.save(savedDeck.getCreator());
                } catch (Exception e) {
                    return false;
                }
                return subscribe(savedDeck, savedDeck.getCreator());
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * Updates a deck in the repository with the given parameters
     * Deleted and blocked decks cannot be updated
     * NOTE: No permission check is done within this method - check before, if execution is allowed!
     *
     * @param deck deck that is to be updated
     * @param name new name of the deck, set to null if no change is desired
     * @param description new description of the deck, set to null if no change is desired
     * @return true if the deck was updated, false otherwise
     */
    public boolean update(Deck deck, String name, String description) {
        if (deck != null && deck.getDeckId() != null) {
            if (deck.isBlocked() || deck.isDeleted()) return false;
            if (name != null) deck.setName(name);
            if (description != null) deck.setDescription(description);
            return save(deck) != null;
        } else {
            return false;
        }
    }

    /**
     * Deletes a deck (soft delete) in the repository
     * Already deleted deck cannot be deleted again
     * NOTE: No permission check is done within this method - check before, if execution is allowed!
     *
     * @param deck deck to delete
     * @return true if deck has been deleted, false otherwise
     */
    public boolean delete(Deck deck) {
        if (deck != null && deck.getDeckId() != null) {
            if (deck.isDeleted()) return false;
            deck.setDeleted(true);
            unsubscribe(deck, deck.getCreator());
            return save(deck) != null;
        } else {
            return false;
        }
    }

    /**
     * Publishes a deck in the repository
     * Already published deck cannot be published again
     * NOTE: No permission check is done within this method - check before, if execution is allowed!
     *
     * @param deck deck to publish
     * @return true if deck has been published, false otherwise
     */
    public boolean publish(Deck deck) {
        if (deck != null && deck.getDeckId() != null) {
            if (deck.isPublished()) return false;
            deck.setPublished(true);
            return save(deck) != null;
        } else {
            return false;
        }
    }

    /**
     * Unpublishes a deck in the repository
     * Already unpublished deck cannot be unpublished again
     * NOTE: No permission check is done within this method - check before, if execution is allowed!
     *
     * @param deck deck to unpublish
     * @return true if deck has been unpublished, false otherwise
     */
    public boolean unpublish(Deck deck) {
        if (deck != null && deck.getDeckId() != null) {
            if (!deck.isPublished()) return false;
            deck.setPublished(false);
            return save(deck) != null;
        } else {
            return false;
        }
    }

    /**
     * Subscribe a person in the repository to a deck in the repository
     * A person that is already subscribed to a deck cannot subscribe again
     *
     * @param deck deck to subscribe to
     * @param person person to subscribe to
     * @return true if the person has been subscribed, false otherwise
     */
    public boolean subscribe(Deck deck, Person person) {
        if (deck != null && deck.getDeckId() != null && person != null && person.getPersonId() != null) {
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
    }

    /**
     * Unsubscribe a person in the repository from a deck in the repository
     * Only persons that have subscribed to a deck can unsubscribe from it
     * The creator of a deck cannot unsubscribe from the deck
     *
     * @param deck deck to unsubscribe from
     * @param person to unsubsubscribe from
     * @return true if the person has been unsubscribed, false otherwise
     */
    public boolean unsubscribe(Deck deck, Person person) {
        if (deck != null && deck.getDeckId() != null && person != null && person.getPersonId() != null) {
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
    }
}
