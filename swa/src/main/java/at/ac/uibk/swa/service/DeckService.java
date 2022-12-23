package at.ac.uibk.swa.service;

import at.ac.uibk.swa.models.Deck;
import at.ac.uibk.swa.models.Permission;
import at.ac.uibk.swa.models.Person;
import at.ac.uibk.swa.repositories.DeckRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
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
        List<Deck> allDecks = deckRepository.findAll();

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
     * Saves a deck to the repository
     * If deck already exists, use one of
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
}
