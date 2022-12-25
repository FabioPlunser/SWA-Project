package at.ac.uibk.swa.service;

import at.ac.uibk.swa.models.Deck;
import at.ac.uibk.swa.models.Permission;
import at.ac.uibk.swa.models.Person;
import at.ac.uibk.swa.repositories.DeckRepository;
import at.ac.uibk.swa.repositories.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.text.html.Option;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service("deckService")
public class DeckService {
    @Autowired
    DeckRepository deckRepository;
    @Autowired
    PersonService personService;
    @Autowired
    PersonRepository personRepository;

    /**
     * Gets all decks from the repository no matter if deleted, blocked, published, etc.
     *
     * @return  a list of all decks
     */
    public List<Deck> getAllDecks() {
        return deckRepository.findAll();
    }

    /**
     * Gets all decks to which a person has subscribed, but might alter description, depending on deck status
     *  - isDeleted: info, that deck has been deleted
     *  - isBlocked: info, that deck has been blocked
     *  - !isPublished: info, that deck has been unpublished, if not creator
     *
     * @param person person that wants to get all the decks
     * @return a list of all decks to which that person has subscribed
     */
    public List<Deck> getAllDecks(Person person) {
        if (person != null) {
            return person.getSavedDecks().stream()
                    .map(d -> {if (!d.getCreator().equals(person) && !d.isPublished()) d.setDescription("Deck has been unpublished"); return d;})
                    .map(d -> {if (d.isBlocked()) d.setDescription("Deck has been blocked"); return d;})
                    .map(d -> {if (d.isDeleted()) d.setDescription("Deck has been deleted"); return d;})
                    .toList();
        } else {
            return new ArrayList<>();
        }
    }

    /**
     * Gets all decks owned by a specific user from the repository
     *
     * @param personId id of the person
     * @return list of owned decks, empty list if none have been found or person has not been found
     */
    public List<Deck> getAllOwnedDecks(UUID personId) {
        Optional<Person> maybePerson = personService.findById(personId);
        if (maybePerson.isPresent()) {
            Person person = maybePerson.get();
            List<Deck> allDecks = getAllDecks(person.getPersonId());
            return allDecks.stream().filter(d -> d.getCreator().equals(person)).toList();
        } else {
            return new ArrayList<>();
        }
    }

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
     * Saves a deck to the repository
     *
     * @param deck deck to save
     * @return true if deck has been saved, false otherwise
     */
    public boolean save(Deck deck) {
        try {
            this.deckRepository.save(deck);
            return true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    /**
     * Updates a deck with the given parameters
     * Deleted and blocked decks cannot be updated
     * NOTE: No permission check is done within this method - check before, if execution is allowed!
     *
     * @param deckId id of the deck to be updated
     * @param name new name of the deck, set to null if no change is desired
     * @param description new description of the deck, set to null if no change is desired
     * @return true if the deck was updated, false otherwise
     */
    public boolean update(UUID deckId, String name, String description) {
        try {
            Optional<Deck> maybeFoundDeck = this.findById(deckId);
            if (maybeFoundDeck.isEmpty()) {
                return false;
            } else if (maybeFoundDeck.get().isBlocked() || maybeFoundDeck.get().isDeleted()) {
                return false;
            } else {
                Deck deck = maybeFoundDeck.get();
                if (name != null) deck.setName(name);
                if (description != null) deck.setDescription(description);
                return save(deck);
            }
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Deletes a deck (soft delete)
     * Already deleted deck cannot be deleted again
     * NOTE: No permission check is done within this method - check before, if execution is allowed!
     *
     * @param deckId id of deck to be deleted
     * @return true if deck has been deleted, false otherwise
     */
    public boolean delete(UUID deckId) {
        try {
            Optional<Deck> maybeFoundDeck = this.findById(deckId);
            if (maybeFoundDeck.isEmpty()) {
                return false;
            } else if (maybeFoundDeck.get().isDeleted()) {
                return false;
            } else {
                Deck deck = maybeFoundDeck.get();
                deck.setDeleted(true);
                return save(deck);
            }
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Blocks a deck
     * Already blocked deck cannot be blocked again
     * NOTE: No permission check is done within this method - check before, if execution is allowed!
     *
     * @param deckId id of deck to be blocked
     * @return true if deck has been blocked, false otherwise
     */
    public boolean block(UUID deckId) {
        try {
            Optional<Deck> maybeFoundDeck = this.findById(deckId);
            if (maybeFoundDeck.isEmpty()) {
                return false;
            } else if (maybeFoundDeck.get().isBlocked()) {
                return false;
            } else {
                Deck deck = maybeFoundDeck.get();
                deck.setBlocked(true);
                return save(deck);
            }
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Unlocks a deck
     * Already unblocked deck cannot be unblocked again
     * NOTE: No permission check is done within this method - check before, if execution is allowed!
     *
     * @param deckId id of deck to be unblocked
     * @return true if deck has been unblocked, false otherwise
     */
    public boolean unblock(UUID deckId) {
        try {
            Optional<Deck> maybeFoundDeck = this.findById(deckId);
            if (maybeFoundDeck.isEmpty()) {
                return false;
            } else if (maybeFoundDeck.get().isBlocked()) {
                return false;
            } else {
                Deck deck = maybeFoundDeck.get();
                deck.setBlocked(false);
                return save(deck);
            }
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Publishes a deck
     * Already published deck cannot be published again
     * NOTE: No permission check is done within this method - check before, if execution is allowed!
     *
     * @param deckId id of deck to be published
     * @return true if deck has been published, false otherwise
     */
    public boolean publish(UUID deckId) {
        try {
            Optional<Deck> maybeFoundDeck = this.findById(deckId);
            if (maybeFoundDeck.isEmpty()) {
                return false;
            } else if (maybeFoundDeck.get().isPublished()) {
                return false;
            } else {
                Deck deck = maybeFoundDeck.get();
                deck.setPublished(true);
                return save(deck);
            }
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Unpublishes a deck
     * Already unpublished deck cannot be unpublished again
     * NOTE: No permission check is done within this method - check before, if execution is allowed!
     *
     * @param deckId id of deck to be unpublished
     * @return true if deck has been unpublished, false otherwise
     */
    public boolean unpublish(UUID deckId) {
        try {
            Optional<Deck> maybeFoundDeck = this.findById(deckId);
            if (maybeFoundDeck.isEmpty()) {
                return false;
            } else if (!maybeFoundDeck.get().isPublished()) {
                return false;
            } else {
                Deck deck = maybeFoundDeck.get();
                deck.setPublished(false);
                return save(deck);
            }
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Subscribe a person to a deck
     * A person that is already subscribed to a deck cannot subscribe again
     *
     * @param deckId id of the deck to subscribe to
     * @param personId id of the person that is to subscribe
     * @return true if the person has been subscribed, false otherwise
     */
    public boolean subscribeToDeck(UUID deckId, UUID personId) {
        Optional<Deck> maybeDeck = findById(deckId);
        Optional<Person> maybePerson = personService.findById(personId);
        if (maybeDeck.isPresent() && maybePerson.isPresent()) {
            Deck deck = maybeDeck.get();
            Person person = maybePerson.get();
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
     * Unsubscribe a person from a deck
     * Only persons that have subscribed to a deck can unsubscribe from it
     * The creator of a deck cannot unsubscribe from the deck
     *
     * @param deckId id of the from which to unsubscribe
     * @param personId id of the person that is to be unsubscribed
     * @return true if the person has been unsubscribed, false otherwise
     */
    public boolean unsubscribeFromDeck(UUID deckId, UUID personId) {
        Optional<Deck> maybeDeck = findById(deckId);
        Optional<Person> maybePerson = personService.findById(personId);
        if (maybeDeck.isPresent() && maybePerson.isPresent()) {
            Deck deck = maybeDeck.get();
            Person person = maybePerson.get();
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
