package at.ac.uibk.swa.models.RestResponses;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.MODULE)
@AllArgsConstructor
public class TokenResponse extends RestResponse implements Serializable {

    private UUID token;

    public TokenResponse(boolean success, UUID token) {
        super(success);
        this.token = token;
    }
}
