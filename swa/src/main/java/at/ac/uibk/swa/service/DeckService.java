package at.ac.uibk.swa.service;

import at.ac.uibk.swa.models.Deck;
import at.ac.uibk.swa.models.Permission;
import at.ac.uibk.swa.models.Person;
import at.ac.uibk.swa.repositories.DeckRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    /**
     * Gets all decks from the repository no matter if deleted, blocked, published, etc.
     *
     * @return  a list of all decks
     */
    public List<Deck> getAllDecks() {
        return deckRepository.findAll();
    }

    /**
     * Gets all decks from the repository that a person can see, depending on permissions
     *  - isDeleted:
     *      - deck is included, if person is subscriber of deck, but description is changed
     *  - isBlocked:
     *      - deck is included, if person is ADMIN
     *      - deck is included, if person is subscriber of deck, but description is changed
     *  - !isPublished:
     *      - deck is included, if person is ADMIN
     *      - deck is included, if person is creator
     *      - deck is included, if person is subscriber of deck, but description is changed
     *
     * @param person person that wants to get all decks
     * @return a list of all decks that person can view
     */
    public List<Deck> getAllDecks(Person person) {
        List<Deck> allDecks = this.getAllDecks();

        List<Deck> deletedDecks = allDecks.stream()
                .filter(d -> d.getAllPersons().contains(person) && d.isDeleted())
                .toList();
        deletedDecks.forEach(d -> d.setDescription("Deck has been deleted"));

        List<Deck> blockedDecks = allDecks.stream()
                .filter(d -> !deletedDecks.contains(d))
                .filter(d ->
                        person.getPermissions().contains(Permission.ADMIN) ||
                        (d.getAllPersons().contains(person) && d.isBlocked())
                )
                .toList();
        if (!person.getPermissions().contains(Permission.ADMIN)) {
            blockedDecks.forEach(d -> d.setDescription("Deck has been blocked"));
        }

        List<Deck> ownedDecks = allDecks.stream()
                .filter(d -> !deletedDecks.contains(d))
                .filter(d -> !blockedDecks.contains(d))
                .filter(d -> d.getCreator().equals(person))
                .toList();

        List<Deck> publishedDecks = allDecks.stream()
                .filter(d -> !deletedDecks.contains(d))
                .filter(d -> !blockedDecks.contains(d))
                .filter(d -> !ownedDecks.contains(d))
                .filter(d ->
                        person.getPermissions().contains(Permission.ADMIN) ||
                        (d.getAllPersons().contains(person) && !d.isPublished())
                )
                .toList();
        if (!person.getPermissions().contains(Permission.ADMIN)) {
            publishedDecks.forEach(d -> d.setDescription("Deck has been unpublished"));
        }

        return Stream.concat(
                Stream.concat(
                        deletedDecks.stream(),
                        blockedDecks.stream()
                ),
                Stream.concat(
                        ownedDecks.stream(),
                        publishedDecks.stream()
                )
        ).toList();
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
     *  update()
     *  delete()
     *  block()
     *  unblock()
     *  publish()
     *  unpublish()
     * instead.
     *
     * @param deck deck to save
     * @return true if deck has been saved, false otherwise
     */
    public boolean save(Deck deck) {
        try {
            this.deckRepository.save(deck);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Updates a deck with the given parameters
     * Deleted and blocked decks cannot be updated
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
}
