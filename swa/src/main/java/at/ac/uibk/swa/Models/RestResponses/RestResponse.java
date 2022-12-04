package at.ac.uibk.swa.Models.RestResponses;

import lombok.Getter;

import java.io.Serializable;

@Getter
public abstract class RestResponse implements Serializable {
    private boolean success;

    public RestResponse(boolean successful) {
        this.success = successful;
    }
}
