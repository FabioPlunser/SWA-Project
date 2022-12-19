package at.ac.uibk.swa.controllers;

import at.ac.uibk.swa.models.Card;
import at.ac.uibk.swa.models.Deck;
import at.ac.uibk.swa.models.annotations.Admin;
import at.ac.uibk.swa.models.restResponses.ListResponse;
import at.ac.uibk.swa.models.restResponses.MessageResponse;
import at.ac.uibk.swa.models.restResponses.RestResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@SuppressWarnings("unused")
public class DeckController {
    @PutMapping("/api/createDeck")
    public RestResponse createDeck(
            @RequestParam(name = "deck") final Deck deck
    ) {
        return new MessageResponse(true, "");
    }

    @PostMapping("/api/updateDeck")
    public RestResponse updateDeck(
            @RequestParam(name = "deck") final Deck deck
    ) {
        return new MessageResponse(true, "");
    }

    @PutMapping("/api/setPublicity")
    public RestResponse setPublicity(
            @RequestParam(name = "deck") final Deck deck
    ) {
        return new MessageResponse(true, "Changed Deck Publicity");
    }

    @DeleteMapping("/api/deleteDeck")
    public RestResponse deleteDeck(
            @RequestParam(name = "deckId") final UUID deckId
    ) {
        return new MessageResponse(true, "");
    }

    @GetMapping("/api/getUserDecks")
    public RestResponse getUserDecks(
            @RequestParam(name = "personId") final UUID personId
    ) {
        return new MessageResponse(true, "getUserDecks" + personId);
    }

    @GetMapping("/api/getPublishedDecks")
    public RestResponse getPublishedDecks() {
        return new MessageResponse(true, "");
    }

    @Admin
    @GetMapping("/api/getAllDecks")
    public RestResponse getAllDecks() {
        return new MessageResponse(true, "getAllDecks");
    }

    /**
     * Gets the Cards from the given Deck which should be learned.
     * A Card should be learned if it's nextLearn-Date is before LocalDateTime.NOW
     *
     * @return A List of Cards that should be learned sorted by nextLearn-Date.
     */
    @GetMapping("/api/getAllCardsToLearn")
    public RestResponse getAllCardsToLearn(
            @RequestParam(name = "deckId") final UUID deckId
    ) {
        return new ListResponse<Card>(List.of());
    }
}