package at.ac.uibk.swa.controllers;

import at.ac.uibk.swa.models.Card;
import at.ac.uibk.swa.models.rest_responses.ListResponse;
import at.ac.uibk.swa.models.rest_responses.MessageResponse;
import at.ac.uibk.swa.models.rest_responses.RestResponse;
import at.ac.uibk.swa.service.CardService;
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
@RestController
public class CardController {

    @Autowired
    private CardService cardService;


    /**
     * Creates a new Card and adds it to the given Deck.
     * @param card
     * @param deckId
     * @return A MessageResponse indicating success or failure.
     */
    @PostMapping("/api/create-card")
    public RestResponse createCard(
            @RequestBody final Card card,
            @RequestParam(name = "deckId") final UUID deckId
            ) {
        if (cardService.create(card, deckId)) {
            return MessageResponse.builder()
                .ok()
                .message("Card " + card.getCardId() + " created in " + deckId)
                .build();
        }
        return MessageResponse.builder()
            .notOk()
            .message("Card " + card.getCardId() + " not created in " + deckId)
            .build();
    }


    /**
     * Updates the given Card.
     * @param card
     * @return A MessageResponse indicating success or failure.
     */
    @PostMapping("/api/update-card")
    public RestResponse updateCard(
            @RequestBody final Card card
    ) {
        if (cardService.update(card.getCardId(), card.getFrontText(), card.getBackText(), card.isFlipped())) {
            return MessageResponse.builder()
                .ok()
                .message("Card " + card.getCardId() + " updated")
                .build();
        }
        return MessageResponse.builder()
            .notOk()
            .message("Card " + card.getCardId() + " not updated")
            .build();
    }

    /**
     * Deletes the given Card.
     * @param cardId
     * @return A MessageResponse indicating success or failure.
     */
    @DeleteMapping("/api/delete-card")
    public RestResponse deleteCard(
            @RequestParam(name = "cardId") final UUID cardId
    ) {
        // TODO: Also delete associated LearningProgresses (maybe cascade delete using orm?)
        if (cardService.delete(cardId)){
            return MessageResponse.builder()
                .ok()
                .message("Card " + cardId + " deleted")
                .build();
        }
        return MessageResponse.builder()
            .notOk()
            .message("Card " + cardId + " not deleted")
            .build();
    }

    /**
     * Returns all Cards of the given Deck.
     * @param deckId
     * @return
     */
    @GetMapping("/api/get-cards-of-deck")
    public RestResponse getCardsOfDeck(
            @RequestParam(name = "deckId") final UUID deckId
    ) {

        Optional<List<Card>> maybeCards = cardService.getAllCards(deckId);
        if (maybeCards.isPresent()) {
            return new ListResponse<>(maybeCards.get());
        }
        return MessageResponse.builder()
            .notOk()
            .message("No Cards found")
            .build();
    }
}
