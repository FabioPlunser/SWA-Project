package at.ac.uibk.swa.Models.RestResponses;

import lombok.Getter;

import java.io.Serializable;

@Getter
public class MessageResponse extends RestResponse implements Serializable {

    private String message;

    public MessageResponse(boolean successful, String message) {
        super(successful);
        this.message = message;
    }
}
