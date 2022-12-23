package at.ac.uibk.swa.service;

import at.ac.uibk.swa.models.Deck;
import at.ac.uibk.swa.models.Permission;
import at.ac.uibk.swa.models.Person;
import at.ac.uibk.swa.repositories.DeckRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Predicate;

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

    public boolean save(Deck deck) {
        try {
            this.deckRepository.save(deck);
            return true;
        } catch (Exception e) {
            return false;
        }
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
        return null;
    }
}
