package at.ac.uibk.swa.controllers;

import at.ac.uibk.swa.models.Card;
import at.ac.uibk.swa.models.restResponses.MessageResponse;
import at.ac.uibk.swa.models.restResponses.RestResponse;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller handling {@link Card} related Information (e.g. creating, updating, deleting {@link Card}s)
 *
 * @author Fabian Magreiter
 */
@RestController
public class CardController {

    @PutMapping("/api/createCard")
    public RestResponse createCard(
            @RequestParam(name = "card") final Card card
    ) {
        return new MessageResponse(true, "Created Card");
    }

    @PutMapping("/api/updateCard")
    public RestResponse updateCard(
            @RequestParam(name = "card") final Card card
    ) {
        return new MessageResponse(true, "Updated Card");
    }

    @DeleteMapping("/api/deleteCard")
    public RestResponse deleteCard(
            @RequestParam(name = "card") final Card card
    ) {
        // TODO: Also delete associated LearningProgresses
        return new MessageResponse(true, "Deleted Card");
    }
}
