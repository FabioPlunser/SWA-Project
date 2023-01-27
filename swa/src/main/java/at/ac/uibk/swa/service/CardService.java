package at.ac.uibk.swa.service;

import at.ac.uibk.swa.config.jwt_authentication.AuthContext;
import at.ac.uibk.swa.models.*;
import at.ac.uibk.swa.repositories.CardRepository;
import at.ac.uibk.swa.repositories.DeckRepository;
import at.ac.uibk.swa.repositories.LearningProgressRepository;
import at.ac.uibk.swa.service.card_service.learning_algorithm.LearningAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Predicate;

@Service("cardService")
public class CardService {
    @Autowired
    private CardRepository cardRepository;
    @Autowired
    private UserDeckService userDeckService;
    @Autowired
    private DeckRepository deckRepository;
    @Autowired
    private LearningProgressRepository learningProgressRepository;

    /**
     * Gets all existing cards for a specific deck and the currently logged in user
     * Depending on deck state:
     *  - !isPublished: only ADMIN and creator will receive cards
     *  - isBlocked:    only ADMIN will receive cards
     *  - isDeleted:    no one will receive cards
     *
     * @param deckId id of the deck for which the cards should be retrieved
     * @return a list of all the cards in the given deck the specific user can access or nothing if deck does not exist
     */
    public Optional<List<Card>> getAllCards(UUID deckId) {
        Optional<Deck> maybeDeck = userDeckService.findById(deckId);
        if (maybeDeck.isPresent()) {
            Deck deck = maybeDeck.get();
            if (deck.isDeleted() ||
                    (deck.isBlocked() &&
                            AuthContext.missingPermission(Permission.ADMIN) &&
                            !deck.getCreator().equals(AuthContext.getCurrentPerson().orElse(null))
                    ) ||
                    (!deck.isPublished() &&
                            AuthContext.missingPermission(Permission.ADMIN) &&
                            !deck.getCreator().equals(AuthContext.getCurrentPerson().orElse(null))
                    )
            ) {
                return Optional.of(new ArrayList<>());
            } else {
                return Optional.of(deck.getCards());
            }
        } else {
            return Optional.empty();
        }
    }

    /**
     * Gets all cards that should be learnt from a specific deck from the repository for the logged in user
     * User must have subscribed to deck
     *
     * @param deckId The ID of the Deck from which to get the cards to learn.
     * @return A List of cards that are supposed to be learned or nothing if either user does not exist or user has
     * not subscribed to deck
     */
    public Optional<List<Card>> getAllCardsToLearn(UUID deckId) {
        Optional<Person> maybePerson = AuthContext.getCurrentPerson();
        // nobody logged in
        if (maybePerson.isEmpty()) {
            return Optional.empty();
        }
        Person person = maybePerson.get();

        // logged in person does not have requested deck in saved decks
        if (person.getSavedDecks().stream().noneMatch(d -> d.getDeckId().equals(deckId))) {
            return Optional.empty();
        }

        return getAllCards(deckId).map(cards -> cards.stream()
                .filter(c -> {
                    // cards that have already been learnt and are due for learning now
                    if (c.getLearningProgress(person).isPresent()) {
                        return c.getLearningProgress(person).get().getNextLearn().isBefore(LocalDateTime.now());
                    }
                    // cards that have never been learnt
                    else {
                        return true;
                    }
                })
                .sorted(
                        // order cards from oldest nextLearnDate to newest
                        Comparator.comparing(c -> {
                            if (c.getLearningProgress(person).isPresent()) {
                                return c.getLearningProgress(person).get().getNextLearn();
                            }
                            // cards without learningProgress (= never learnt) are assumed to be due right now
                            else {
                                return LocalDateTime.now();
                            }
                        })
                )
                .toList()
        )
        // return an empty list if no cards are found
        .or(() -> Optional.of(new ArrayList<>()));
    }

    /**
     * Finds a card within the repository by its id
     *
     * @param cardId id of the card to be found
     * @return card with given id (if found), otherwise nothing
     */
    public Optional<Card> findById(UUID cardId) {
        return cardRepository.findById(cardId);
    }

    /**
     * Get the Learning Progress associated with the given Card using the ID and the currently logged in user.
     *
     * @param cardId The ID of the card to get the Learning Progress from.
     * @return The Learning Progress associated with the giv
     */
    public Optional<LearningProgress> getLearningProgress(UUID cardId) {
        Optional<Card> maybeCard = cardRepository.findById(cardId);
        Optional<Person> maybePerson = AuthContext.getCurrentPerson();
        if (maybeCard.isPresent() && maybePerson.isPresent()) {
            Person person = maybePerson.get();
            return maybeCard.get().getLearningProgress(person);
        } else {
            return Optional.empty();
        }
    }

    /**
     * Give feedback on the learning of a specific card for the currently logged-in user.
     * User must have card due for learning
     *
     * @param cardId The ID of the card to learn.
     * @param difficulty The difficulty that the user gave.
     * @return true if the card was learnt, false otherwise.
     */
    public boolean learn(UUID cardId, int difficulty) {
        Optional<Person> maybePerson = AuthContext.getCurrentPerson();
        if (maybePerson.isPresent()) {
            Person person = maybePerson.get();
            Optional<Card> maybeCard = findById(cardId);
            if (maybeCard.isPresent()) {
                Card card = maybeCard.get();
                Optional<List<Card>> maybeCardsToLearn = getAllCardsToLearn(card.getDeck().getDeckId());
                if (maybeCardsToLearn.isPresent() && maybeCardsToLearn.get().contains(card)) {
                    LearningProgress newLearningProgress = card.updateLearningProgress(
                            person,
                            learningProgress -> LearningAlgorithm.getUpdatedLearningProgress(learningProgress , difficulty)
                    );
                    try {
                        learningProgressRepository.save(newLearningProgress);
                        return save(card) != null;
                    } catch (Exception e) {
                        // learning progress not saved
                        return false;
                    }
                } else {
                    // card not due for learning
                    return false;
                }
            } else {
                // card does not exist
                return false;
            }
        } else {
            // user not logged in
            return false;
        }
    }

    /**
     * creates a new card within the repository
     * creating user must own the deck specified within the card
     * deck must not be blocked or deleted
     *
     * @param card card to be created
     * @return true if card has been created, false otherwise
     */
    @Transactional
    public boolean create(Card card, UUID deckId) {
        if (card != null && card.getCardId() == null) {
            Deck deck = getDeckIfWriteAccess(deckId).orElse(null);
            if (deck != null) {
                card.setDeck(deck);
                return save(card) != null;
            } else {
                // logged-in user has not created the given deck or deck is blocked/deleted - therefore no write access
                return false;
            }
        } else {
            // no card given or already created card given
            return false;
        }
    }

    /**
     * Saves a card to the repository
     *
     * @param card card to be saved
     * @return saved card if it has been saved, null otherwise
     */
    private Card save(Card card) {
        try {
            return cardRepository.save(card);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Updates a card with the given parameters
     * Checks write access to deck of card
     *
     * @param cardId id of the card to update
     * @param frontText new front text of the card, set to null if no change is desired
     * @param backText new back text of the card, set to null if no change is desired
     * @param isFlipped card flipped or not flipped
     * @return true if card has been updated, false otherwise
     */
    public boolean update(UUID cardId, String frontText, String backText, boolean isFlipped) {
        Optional<Card> maybeCard = findById(cardId);
        if (maybeCard.isPresent()) {
            Card card = maybeCard.get();
            if (getDeckIfWriteAccess(card.getDeck().getDeckId()).isPresent()) {
                if (frontText !=  null) card.setFrontText(frontText);
                if (backText != null) card.setBackText(backText);
                card.setFlipped(isFlipped);
                return save(card) != null;
            } else {
                // no write access to given deck
                return false;
            }
        } else {
            // card not found
            return false;
        }
    }

    /**
     * Deletes a card from the repository (hard delete)
     * Checks write access to deck of card
     *
     * @param cardId id of the card to be deleted
     * @return true if card has been updated, false otherwise
     */
    public boolean delete(UUID cardId) {
        Optional<Card> maybeCard = findById(cardId);
        if (maybeCard.isPresent()) {
            Card card = maybeCard.get();
            Deck deck = getDeckIfWriteAccess(card.getDeck().getDeckId()).orElse(null);
            if (deck != null) {
                deck.getCards().remove(card);
                try {
                    deckRepository.save(deck);
                    cardRepository.delete(card);
                    return true;
                } catch (Exception e) {
                    return false;
                }
            } else {
                // no write access to given deck
                return false;
            }
        } else {
            // card not found
            return false;
        }
    }

    /**
     * Check if the currently logged in user has write access to the given deck
     *
     * @param deckId id of the deck to check
     * @return the deck if write access, otherwise nothing
     */
    private Optional<Deck> getDeckIfWriteAccess(UUID deckId) {
        Optional<Person> maybePerson = AuthContext.getCurrentPerson();
        if (maybePerson.isPresent()) {
            Person person = maybePerson.get();
            return person.getCreatedDecks().stream()
                    .filter(Predicate.not(Deck::isDeleted))
                    .filter(Predicate.not(Deck::isBlocked))
                    .filter(d -> d.getDeckId().equals(deckId))
                    .findFirst();
        } else {
            // no user authenticated
            return Optional.empty();
        }
    }
}
