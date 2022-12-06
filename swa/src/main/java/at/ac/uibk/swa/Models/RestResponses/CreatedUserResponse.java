package at.ac.uibk.swa.Models.RestResponses;

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

    public CreatedUserResponse(String username, UUID userId) {
        this(username, userId, null);
    }

    public CreatedUserResponse(String username, UUID userId, UUID token) {
        super(true);
        this.username = username;
        this.userId = userId;
        this.token = token;
    }
}
