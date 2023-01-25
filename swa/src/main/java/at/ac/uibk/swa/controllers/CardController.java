package at.ac.uibk.swa.controllers;

import at.ac.uibk.swa.models.Card;
import at.ac.uibk.swa.models.annotations.ApiRestController;
import at.ac.uibk.swa.models.rest_responses.ListResponse;
import at.ac.uibk.swa.models.rest_responses.MessageResponse;
import at.ac.uibk.swa.models.rest_responses.RestResponse;
import at.ac.uibk.swa.service.CardService;
import at.ac.uibk.swa.service.UserDeckService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Controller handling {@link Card} related Information (e.g. creating, updating, deleting {@link Card}s)
 *
 * @author Fabian Magreiter
 */
@ApiRestController
public class CardController {

    @Autowired
    private CardService cardService;
    @Autowired
    private UserDeckService userDeckService;


    /**
     * Creates a new Card and adds it to the given Deck.
     * @param card
     * @param deckId
     * @return A MessageResponse indicating success or failure.
     */
    @PostMapping("/create-card")
    public RestResponse createCard(
            @RequestBody final Card card,
            @RequestParam(name = "deckId") final UUID deckId
            ) {
        if (cardService.create(card, deckId)) {
            return MessageResponse.builder()
                .ok()
                .message("Card created in " + userDeckService.getDeckNameIfPresent(deckId))
                .build();
        }
        return MessageResponse.builder()
            .error()
            .message("Card not created")
            .build();
    }


    /**
     * Updates the given Card.
     * @param card
     * @return A MessageResponse indicating success or failure.
     */
    @PostMapping("/update-card")
    public RestResponse updateCard(
            @RequestBody final Card card
    ) {
        if (cardService.update(card.getCardId(), card.getFrontText(), card.getBackText(), card.isFlipped())) {
            return MessageResponse.builder()
                .ok()
                .message("Card updated")
                .build();
        }
        return MessageResponse.builder()
            .error()
            .message("Card not updated")
            .build();
    }

    /**
     * Deletes the given Card.
     * @param cardId
     * @return A MessageResponse indicating success or failure.
     */
    @DeleteMapping("/delete-card")
    public RestResponse deleteCard(
            @RequestParam(name = "cardId") final UUID cardId
    ) {
        if (cardService.delete(cardId)){
            return MessageResponse.builder()
                .ok()
                .message("Card deleted")
                .build();
        }
        return MessageResponse.builder()
            .error()
            .message("Card not deleted")
            .build();
    }

    /**
     * Returns all Cards of the given Deck.
     * @param deckId
     * @return
     */
    @GetMapping("/get-cards-of-deck")
    public RestResponse getCardsOfDeck(
            @RequestParam(name = "deckId") final UUID deckId
    ) {

        Optional<List<Card>> maybeCards = cardService.getAllCards(deckId);
        if (maybeCards.isPresent()) {
            return new ListResponse<>(maybeCards.get());
        }
        return MessageResponse.builder()
            .error()
            .message("No Cards found")
            .build();
    }
}
