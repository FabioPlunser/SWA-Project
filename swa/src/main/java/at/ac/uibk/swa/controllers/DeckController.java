package at.ac.uibk.swa.controllers;

import at.ac.uibk.swa.models.Card;
import at.ac.uibk.swa.models.Deck;
import at.ac.uibk.swa.models.Permission;
import at.ac.uibk.swa.models.annotations.AnyPermission;
import at.ac.uibk.swa.models.rest_responses.*;
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
        if (!userDeckService.create(deck)) {
            return MessageResponse.builder().error().message("Deck " + deck.getName() + " could not be created.").build();
        }

        return MessageResponse.builder().ok().message("Deck " + deck.getName() + " created successfully.").build();
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
        if (userDeckService.update(deck)) {
            return MessageResponse.builder()
                    .ok()
                    .message("Deck " + deck.getName() + " updated")
                    .build();
        }
        return MessageResponse.builder()
                .error()
                .message("Deck " + deck.getName() + " not updated")
                .build();
    }


    /**
     * Sets given deck to public.
     * @param deckId
     * @return
     */
    @PostMapping("/api/publish-deck")
    public RestResponse publish(
            @RequestParam(name = "deckId") final UUID deckId
    ) {
        if (userDeckService.publish(deckId)) {
            return MessageResponse.builder()
                    .ok()
                    .message("Deck " + getDeckNameIfPresent(deckId) + " published")
                    .build();
        }
        return MessageResponse.builder()
                .error()
                .message("Deck " + getDeckNameIfPresent(deckId) + " publicity not changed")
                .build();
    }

    /**
     * Sets given deck to private.
     * @param deckId
     * @return
     */
    @PostMapping("/api/unpublish-deck")
    public RestResponse unpublish(
            @RequestParam(name = "deckId") final UUID deckId
    ) {
        if (userDeckService.unpublish(deckId)) {
            return MessageResponse.builder()
                    .ok()
                    .message("Deck " + getDeckNameIfPresent(deckId) + " unpublished")
                    .build();
        }
        return MessageResponse.builder()
                .error()
                .message("Deck " + getDeckNameIfPresent(deckId) + " publicity not changed")
                .build();
    }


    /**
     * Subscribes the current User to the given Deck.
     * @param deckId
     * @return
     */
    @PostMapping("/api/subscribe-deck")
    public RestResponse subscribeDeck(
            @RequestParam(name = "deckId") final UUID deckId
    ) {
        if (userDeckService.subscribe(deckId)) {
            return MessageResponse.builder()
                    .ok()
                    .message("Deck " + getDeckNameIfPresent(deckId) + " subscribed")
                    .build();
        }
        return MessageResponse.builder()
                .error()
                .message("Deck " + getDeckNameIfPresent(deckId) + " not subscribed")
                .build();
    }

    /**
     * Unsubscribes the current User from the given Deck.
     * @param deckId
     * @return
     */
    @PostMapping("/api/unsubscribe-deck")
    public RestResponse unsubscribeDeck(
            @RequestParam(name = "deckId") final UUID deckId
    ) {
        if (userDeckService.unsubscribe(deckId)) {
            return MessageResponse.builder()
                    .ok()
                    .message("Deck " + getDeckNameIfPresent(deckId) + " unsubscribed")
                    .build();
        }
        return MessageResponse.builder()
                .error()
                .message("Deck " + getDeckNameIfPresent(deckId) + " not unsubscribed")
                .build();
    }

    /**
     * Blocks the given Deck.
     * Only Admins can block Decks.
     * @param deckId
     * @return
     */
    @AnyPermission(Permission.ADMIN)
    @PostMapping("/api/block-deck")
    public RestResponse blockDeck(
            @RequestParam(name = "deckId") final UUID deckId
    ) {
        if (adminDeckService.block(deckId)) {
            return MessageResponse.builder()
                    .ok()
                    .message("Deck " + getDeckNameIfPresent(deckId) + " blocked")
                    .build();
        }
        return MessageResponse.builder()
                .error()
                .message("Deck " + getDeckNameIfPresent(deckId) + " not blocked")
                .build();
    }

    /**
     * Unblocks the given Deck.
     * Only Admins can unblock Decks.
     * @param deckId
     * @return
     */
    @AnyPermission(Permission.ADMIN)
    @PostMapping("/api/unblock-deck")
    public RestResponse unblockDeck(
            @RequestParam(name = "deckId") final UUID deckId
    ) {
        if (adminDeckService.unblock(deckId)) {
            return MessageResponse.builder()
                    .ok()
                    .message("Deck " + getDeckNameIfPresent(deckId) + " unblocked")
                    .build();
        }
        return MessageResponse.builder()
                .error()
                .message("Deck " + getDeckNameIfPresent(deckId) + " not unblocked")
                .build();
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
            return MessageResponse.builder()
                    .ok()
                    .message("Deck " + getDeckNameIfPresent(deckId) + " deleted")
                    .build();
        }
        return MessageResponse.builder()
                .error()
                .message("Deck " + getDeckNameIfPresent(deckId) + " not deleted")
                .build();
    }

    /**
     * Gets all Decks that the current User is subscribed to or created.
     * @return A List of Decks.
     */
    @GetMapping("/api/get-user-decks")
    public RestResponse getUserDecks()  {
        Optional<List<Deck>> maybeDecks = userDeckService.getAllViewableDecks();
        if (maybeDecks.isPresent()) {
            return new UserDeckListResponse(maybeDecks.get());
        }
        return MessageResponse.builder()
                .error()
                .message("Could not get decks")
                .build();
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
            return new UserDeckListResponse(maybeDecks.get());
        }
        return MessageResponse.builder()
                .error()
                .message("Could not get decks")
                .build();
    }

    /**
     * Gets all Decks that the current User is subscribed to.
     * @return A List of Decks.
     */
    @GetMapping("/api/get-subscribed-decks")
    public RestResponse getSubscribedDecks()  {
        Optional<List<Deck>> maybeDecks = userDeckService.getAllSubscribedDecks();
        if (maybeDecks.isPresent()) {
            return new UserDeckListResponse(maybeDecks.get());
        }
        return MessageResponse.builder()
                .error()
                .message("Could not get decks")
                .build();
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
        return MessageResponse.builder()
                .error()
                .message("Could not get decks")
                .build();
    }


    /**
     * Get all Decks that are available for subscription
     * Does not include decks to which the requesting user has already subscribed
     *
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
        return new DeckListResponse(adminDeckService.findAll());
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
        return MessageResponse.builder()
                .error()
                .message("Could not get cards")
                .build();

    }

    private String getDeckNameIfPresent(UUID deckId) {
        String name = "";
        Optional<Deck> maybeDeck = userDeckService.findById(deckId);
        if (maybeDeck.isPresent()) {
            name = maybeDeck.get().getName();
        }
        return name;
    }
}

