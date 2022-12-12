package at.ac.uibk.swa.models.RestResponses;

import lombok.Getter;

import java.io.Serializable;
import java.util.UUID;

@Getter
public class TokenResponse extends RestResponse implements Serializable {

    private UUID token;

    public TokenResponse(UUID token) {
        super(true);
        this.token = token;
    }
}
