package at.ac.uibk.swa.controllers;

import at.ac.uibk.swa.models.Card;
import at.ac.uibk.swa.models.rest_responses.MessageResponse;
import at.ac.uibk.swa.models.rest_responses.RestResponse;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import at.ac.uibk.swa.service.CardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/api/create-card")
    public RestResponse createCard(
            @RequestBody final Card card,
            @RequestParam(name = "deckId") final UUID deckId
            ) {
        if (cardService.create(card, deckId)) {
            return new MessageResponse(true, "Card created");
        }
        return new MessageResponse(false, "Card not created");
    }

    @PutMapping("/api/update-card")
    public RestResponse updateCard(
            @RequestBody final Card card
    ) {
        if (cardService.update(card.getCardId(), card.getFrontText(), card.getBackText(), card.isFlipped())) {
            return new MessageResponse(true, "Card updated");
        }
        return new MessageResponse(false, "Card not updated");
    }

    @DeleteMapping("/api/delete-card")
    public RestResponse deleteCard(
            @RequestParam(name = "cardId") final UUID cardId
    ) {
        // TODO: Also delete associated LearningProgresses (maybe cascade delete using orm?)
        if (cardService.delete(cardId)){
            return new MessageResponse(true, "Card deleted");
        }
        return new MessageResponse(false, "Card not deleted");
    }
}
