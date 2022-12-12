package at.ac.uibk.swa.models.RestResponses;

import at.ac.uibk.swa.models.Authenticable;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

import java.io.Serializable;
import java.util.UUID;

@Getter
public class CreatedUserResponse extends RestResponse implements Serializable {

    private String username;
    private UUID userId;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private UUID token;

    public CreatedUserResponse(String username, UUID userId, UUID token) {
        super(true);
        this.username = username;
        this.userId = userId;
        this.token = token;
    }

    public CreatedUserResponse(String username, UUID userId) {
        this(username, userId, null);
    }

    public CreatedUserResponse(Authenticable authenticable) {
        this(authenticable.getUsername(), authenticable.getId(), authenticable.getToken());
    }
}
