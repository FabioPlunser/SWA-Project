package at.ac.uibk.swa.models.restResponses;

import at.ac.uibk.swa.models.Authenticable;
import at.ac.uibk.swa.models.Permission;
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
    private UUID personId;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private UUID token;
    private List<Permission> permissions;

    public CreatedUserResponse(Authenticable authenticable) {
        super(true);
        this.id = authenticable.getId();
        this.username = authenticable.getUsername();
        this.personId = authenticable.getId();
        this.token = authenticable.getToken();
        this.permissions = authenticable.getPermissions();
    }
}
