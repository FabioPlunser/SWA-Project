package at.ac.uibk.swa.controllers;

import at.ac.uibk.swa.models.Card;
import at.ac.uibk.swa.models.Deck;
import at.ac.uibk.swa.models.Permission;
import at.ac.uibk.swa.models.annotations.AnyPermission;
import at.ac.uibk.swa.models.rest_responses.ListResponse;
import at.ac.uibk.swa.models.rest_responses.MessageResponse;
import at.ac.uibk.swa.models.rest_responses.RestResponse;
import at.ac.uibk.swa.service.AdminDeckService;
import at.ac.uibk.swa.service.CardService;
import at.ac.uibk.swa.service.UserDeckService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Controller handling {@link Deck} related Information (e.g. creating, updating, deleting {@link Deck}s)
 *
 * @author Fabian Magreiter
 */
@RestController
@SuppressWarnings("unused")
public class DeckController {

    @Autowired
    private UserDeckService userDeckService;
    @Autowired
    private AdminDeckService adminDeckService;
    @Autowired
    private CardService cardService;


    /**
     * Creates a new Deck with all the given Cards.
     * @param deck
     * @return A MessageResponse indicating success or failure.
     */
    @PostMapping("/api/create-deck")
    public RestResponse createDeck(
            @RequestBody final Deck deck
    ) {
        //TODO doesn't include creating the cards yet needs to be done in userDeckService.create
        if (!userDeckService.create(deck)) {
            return new MessageResponse(false, "Deck not created");
        }

        return new MessageResponse(true, "Deck created");
    }

    /**
     * Updates the given Deck and cards of that Deck.
     * The Deck must be owned by the current User.
     *
     * @param deck The Deck to update.
     * @return A MessageResponse indicating success or failure.
     */
    @PostMapping("/api/update-deck")
    public RestResponse updateDeck(
            @RequestBody final Deck deck
    ) {
        //TODO also update the cards
        if (userDeckService.update(deck.getDeckId(), deck.getName(), deck.getDescription())) {
            return new MessageResponse(true, "Deck updated");
        }
        return new MessageResponse(false, "Deck not updated");
    }


    /**
     * Sets given deck to public.
     * @param deckId
     * @return
     */
    @PutMapping("/api/publish")
    public RestResponse publish(
            @RequestParam(name = "deckId") final UUID deckId
    ) {
        if (userDeckService.publish(deckId)) {
            return new MessageResponse(true, "Deck publicity changed");
        }
        return new MessageResponse(false, "Deck publicity not changed");
    }

    /**
     * Sets given deck to private.
     * @param deckId
     * @return
     */
    @PutMapping("/api/unpublish")
    public RestResponse unpublish(
            @RequestParam(name = "deckId") final UUID deckId
    ) {
        if (userDeckService.unpublish(deckId)) {
            return new MessageResponse(true, "Deck publicity changed");
        }
        return new MessageResponse(false, "Deck publicity not changed");
    }


    /**
     * Subscribes the current User to the given Deck.
     * @param deckId
     * @return
     */
    @PutMapping("/api/subscribe-deck")
    public RestResponse subscribeDeck(
            @RequestParam(name = "deckId") final UUID deckId
    ) {
        if (userDeckService.subscribe(deckId)) {
            return new MessageResponse(true, "Deck subscribed");
        }
        return new MessageResponse(false, "Deck not subscribed");
    }

    /**
     * Unsubscribes the current User from the given Deck.
     * @param deckId
     * @return
     */
    @PutMapping("/api/unsubscribe-deck")
    public RestResponse unsubscribeDeck(
            @RequestParam(name = "deckId") final UUID deckId
    ) {
        if (userDeckService.unsubscribe(deckId)) {
            return new MessageResponse(true, "Deck unsubscribed");
        }
        return new MessageResponse(false, "Deck not unsubscribed");
    }

    /**
     * Blocks the given Deck.
     * Only Admins can block Decks.
     * @param deckId
     * @return
     */
    @AnyPermission(Permission.ADMIN)
    @PutMapping("/api/block-deck")
    public RestResponse blockDeck(
            @RequestParam(name = "deckId") final UUID deckId
    ) {
        if (adminDeckService.block(deckId)) {
            return new MessageResponse(true, "Deck blocked");
        }
        return new MessageResponse(false, "Deck not blocked");
    }

    /**
     * Unblocks the given Deck.
     * Only Admins can unblock Decks.
     * @param deckId
     * @return
     */
    @AnyPermission(Permission.ADMIN)
    @PutMapping("/api/unblock-deck")
    public RestResponse unblockDeck(
            @RequestParam(name = "deckId") final UUID deckId
    ) {
        if (adminDeckService.unblock(deckId)) {
            return new MessageResponse(true, "Deck unblocked");
        }
        return new MessageResponse(false, "Deck not unblocked");
    }

    /**
     * Deletes the given Deck.
     * @param deckId
     * @return
     */
    @DeleteMapping("/api/delete-deck")
    public RestResponse deleteDeck(
            @RequestParam(name = "deckId") final UUID deckId
    ) {
        if (userDeckService.delete(deckId)) {
            return new MessageResponse(true, "Deck deleted");
        }
        return new MessageResponse(false, "Deck not deleted");
    }

    /**
     * Gets all Decks that the current User is subscribed to or created.
     * @return A List of Decks.
     */
    @GetMapping("/api/get-user-decks")
    public RestResponse getUserDecks()  {
        Optional<List<Deck>> maybeDecks = userDeckService.getSavedNotOwnedDecks();
        if (maybeDecks.isPresent()) {
            return new ListResponse<>(maybeDecks.get());
        }
        return new MessageResponse(false, "Could not get decks");
    }

    /**
     * Gets all Decks that given user has subscribed to or created.
     * @return A List of Decks.
     */
    @AnyPermission(Permission.ADMIN)
    @GetMapping("/api/get-given-user-decks")
    public RestResponse getGivenUserDecks(
            @RequestParam(name = "personId") final UUID personId
    ){
        Optional<List<Deck>> maybeDecks = userDeckService.getDecksOfGivenPerson(personId);
        if (maybeDecks.isPresent()) {
            return new ListResponse<>(maybeDecks.get());
        }
        return new MessageResponse(false, "Could not get decks");
    }

    /**
     * Gets all Decks that the current User is subscribed to.
     * @return A List of Decks.
     */
    @GetMapping("/api/get-subscribed-decks")
    public RestResponse getSubscribedDecks()  {
        Optional<List<Deck>> maybeDecks = userDeckService.getAllSavedDecks();
        if (maybeDecks.isPresent()) {
            return new ListResponse<>(maybeDecks.get());
        }
        return new MessageResponse(false, "Could not get decks");
    }

    /**
     * Gets all Decks that the current User created.
     * @return A List of Decks.
     */
    @GetMapping("/api/get-created-decks")
    public RestResponse getCreatedDecks()  {
        Optional<List<Deck>> maybeDecks = userDeckService.getAllOwnedDecks();
        if (maybeDecks.isPresent()) {
            return new ListResponse<>(maybeDecks.get());
        }
        return new MessageResponse(false, "Could not get decks");
    }


    /**
     * Get all Decks a user has set to public.
     * @return A List of Decks.
     */
    @GetMapping("/api/get-published-decks")
    public RestResponse getPublishedDecks() {
        return new ListResponse<>(userDeckService.findAllAvailableDecks());
    }

    /**
     * Gets all Decks.
     * @return A List of Decks.
     */
    @AnyPermission(Permission.ADMIN)
    @GetMapping("/api/get-all-decks")
    public RestResponse getAllDecks() {
        return new ListResponse<>(adminDeckService.findAll());
    }

    /**
     * Gets the Cards from the given Deck which should be learned.
     * A Card should be learned if it's nextLearn-Date is before {@link LocalDateTime#now()}
     *
     * @return A List of Cards that should be learned sorted by nextLearn-Date.
     */
    @GetMapping("/api/get-all-cards-to-learn")
    public RestResponse getAllCardsToLearn(
            @RequestParam(name = "deckId") final UUID deckId
    ) {
        Optional<List<Card>> maybeCards = cardService.getAllCardsToLearn(deckId);
        if (maybeCards.isPresent()) {
            List<Card> cards = maybeCards.get();
            return new ListResponse<>(cards);
        }
        return new MessageResponse(false, "getAllCardsToLearn failed");

    }
}

