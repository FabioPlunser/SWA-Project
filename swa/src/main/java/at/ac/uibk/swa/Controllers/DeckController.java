package at.ac.uibk.swa.Controllers;

import at.ac.uibk.swa.Models.Annotations.Admin;
import at.ac.uibk.swa.Models.Deck;
import at.ac.uibk.swa.Models.RestResponses.MessageResponse;
import at.ac.uibk.swa.Models.RestResponses.RestResponse;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
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
        return new MessageResponse(true, "");
    }

    @GetMapping("/api/getPublishedDecks")
    public RestResponse getPublishedDecks() {
        return new MessageResponse(true, "");
    }

    @Admin
    @GetMapping("/api/getAllDecks")
    public RestResponse getAllDecks() {
        return new MessageResponse(true, "");
    }

    @GetMapping("/api/learn")
    public RestResponse learn(
            @RequestParam(name = "cardId") final Deck cardId,
            @RequestParam(name = "g") final int g
    ) {
        return new MessageResponse(true, "");
    }
}
