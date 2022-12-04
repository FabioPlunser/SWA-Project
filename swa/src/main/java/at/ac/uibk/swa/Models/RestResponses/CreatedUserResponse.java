package at.ac.uibk.swa.Models.RestResponses;

import lombok.Getter;

import java.io.Serializable;
import java.util.UUID;

@Getter
public class CreatedUserResponse extends RestResponse implements Serializable {

    private String username;
    private UUID userId;

    public CreatedUserResponse(String username, UUID userId) {
        super(true);
        this.username = username;
        this.userId = userId;
    }
}
