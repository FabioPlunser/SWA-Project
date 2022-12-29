package at.ac.uibk.swa.models.rest_responses;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@NoArgsConstructor(access = AccessLevel.MODULE)
@AllArgsConstructor
public class MessageResponse extends RestResponse implements Serializable {

    private String message;

    public MessageResponse(boolean successful, String message) {
        super(successful);
        this.message = message;
    }
}
