package at.ac.uibk.swa.service;

import at.ac.uibk.swa.models.Deck;
import at.ac.uibk.swa.repositories.DeckRepository;
import at.ac.uibk.swa.repositories.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service("adminDeckService")
public class AdminDeckService {
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
     * Blocks a deck in the repository
     * Already blocked deck cannot be blocked again
     * NOTE: No permission check is done within this method - check before, if execution is allowed!
     *
     * @param deck deck to be blockes
     * @return true if deck has been blocked, false otherwise
     */
    public boolean block(Deck deck) {
        if (deck != null && deck.getDeckId() != null) {
            if (deck.isBlocked()) return false;
            deck.setBlocked(true);
            return save(deck) != null;
        } else {
            return false;
        }
    }

    /**
     * Unblocks a deck in the repository
     * Already unblocked deck cannot be unblocked again
     * NOTE: No permission check is done within this method - check before, if execution is allowed!
     *
     * @param deck deck to unblock
     * @return true if deck has been unblocked, false otherwise
     */
    public boolean unblock(Deck deck) {
        if (deck != null && deck.getDeckId() != null) {
            if (!deck.isBlocked()) return false;
            deck.setBlocked(false);
            return save(deck) != null;
        } else {
            return false;
        }
    }
}
