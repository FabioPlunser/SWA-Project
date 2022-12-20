package at.ac.uibk.swa.controllers;

import at.ac.uibk.swa.models.Deck;
import at.ac.uibk.swa.models.restResponses.MessageResponse;
import at.ac.uibk.swa.models.restResponses.RestResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
public class LearnController {
    @PutMapping("/api/learn")
    @ResponseStatus(HttpStatus.OK)
    public RestResponse learn(
            @RequestParam(name = "cardId") final Deck cardId,
            @RequestParam(name = "g") final int g
    ) {
        return new MessageResponse(true, "Learned");
    }
}
