package at.ac.uibk.swa.service;

import at.ac.uibk.swa.models.Deck;
import at.ac.uibk.swa.repositories.DeckRepository;
import at.ac.uibk.swa.repositories.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;

@Service("adminDeckService")
public class AdminDeckService {
    @Autowired
    DeckRepository deckRepository;
    @Autowired
    PersonRepository personRepository;

    /**
     * Finds a deck within the repository by its id
     * NOTE: Only call this method within admin API routes
     *
     * @param id id of the deck to be return
     * @return deck with given id (if found), otherwise nothing
     */
    public Optional<Deck> findById(UUID id) {
        return deckRepository.findById(id);
    }

    /**
     * Finds all decks within the repository, except deleted decks
     * NOTE: Only call this method within admin API routes
     *
     * @return list of all found decks
     */
    public List<Deck> findAll() {
        return deckRepository.findAll().stream()
                .filter(Predicate.not(Deck::isDeleted))
                .toList();
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
     * NOTE: Only call this method within admin API routes
     *
     * @param deckId if of the deck to be blocked
     * @return true if deck has been blocked, false otherwise
     */
    //TODO: why return false if deck is already blocked and not just pass?
    public boolean block(UUID deckId) {
        Optional<Deck> maybeDeck = findById(deckId);
        if (maybeDeck.isPresent()) {
            Deck deck = maybeDeck.get();
            if (deck != null && deck.getDeckId() != null) {
                if (deck.isBlocked()) return false;
                deck.setBlocked(true);
                return save(deck) != null;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * Unblocks a deck in the repository
     * Already unblocked deck cannot be unblocked again
     * NOTE: Only call this method within admin API routes
     *
     * @param deckId id of the deck to unblock
     * @return true if deck has been unblocked, false otherwise
     */
    public boolean unblock(UUID deckId) {
        Optional<Deck> maybeDeck = findById(deckId);
        if (maybeDeck.isPresent()) {
            Deck deck = maybeDeck.get();
            if (deck != null && deck.getDeckId() != null) {
                if (!deck.isBlocked()) return false;
                deck.setBlocked(false);
                return save(deck) != null;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
}
