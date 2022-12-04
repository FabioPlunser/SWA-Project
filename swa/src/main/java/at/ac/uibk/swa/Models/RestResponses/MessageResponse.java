package at.ac.uibk.swa.Models.RestResponses;

import java.io.Serializable;

public class MessageResponse extends RestResponse implements Serializable {

    private String message;

    public MessageResponse(boolean successful, String message) {
        super(successful);
        this.message = message;
    }
}
