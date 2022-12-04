package at.ac.uibk.swa.Models.RestResponses;

import lombok.Getter;

import java.io.Serializable;

@Getter
public class AuthFailedResponse extends RestResponse implements Serializable {

    private String message;

    public AuthFailedResponse(String message) {
        super(false);
        this.message = message;
    }
}
