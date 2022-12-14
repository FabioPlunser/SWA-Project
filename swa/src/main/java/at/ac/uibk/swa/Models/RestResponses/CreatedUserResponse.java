package at.ac.uibk.swa.Models.RestResponses;

import at.ac.uibk.swa.Models.Authenticable;
import at.ac.uibk.swa.Models.Permission;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.MODULE)
@AllArgsConstructor
public class CreatedUserResponse extends RestResponse implements Serializable {

    private UUID id;
    private String username;
    private UUID userId;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private UUID token;
    private List<Permission> permissions;

    public CreatedUserResponse(Authenticable authenticable) {
        this(
                authenticable.getId(),
                authenticable.getUsername(),
                authenticable.getId(),
                authenticable.getToken(),
                authenticable.getPermissions()
        );
    }
}
