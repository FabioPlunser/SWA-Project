package at.ac.uibk.swa.controllers;

import at.ac.uibk.swa.models.Card;
import at.ac.uibk.swa.models.rest_responses.MessageResponse;
import at.ac.uibk.swa.models.rest_responses.RestResponse;
import at.ac.uibk.swa.service.CardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * Controller used for learning {@link Card}s.
 *
 * @author Fabian Magreiter
 */
@RestController
public class LearnController {

    @Autowired
    private CardService cardService;

    @PutMapping("/api/learn")
    @ResponseStatus(HttpStatus.OK)
    public RestResponse learn(
            @RequestParam(name = "cardId") final UUID cardId,
            @RequestParam(name = "g") final int g
    ) {
        boolean learned = cardService.learn(cardId, g);
        if (learned) {
            return new MessageResponse(true, "Learned");
        }
        return new MessageResponse(false, "Not learned");
    }
}
