package at.ac.uibk.swa.models.rest_responses;

import at.ac.uibk.swa.models.Authenticable;
import at.ac.uibk.swa.models.Permission;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Set;
import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.MODULE)
public class CreatedUserResponse extends RestResponse implements Serializable {

    private UUID id;
    private String username;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private UUID token;
    private Set<Permission> permissions;

    public CreatedUserResponse(Authenticable authenticable) {
        super.setSuccess(true);
        this.id = authenticable.getId();
        this.username = authenticable.getUsername();
        this.token = authenticable.getToken();
        this.permissions = authenticable.getPermissions();
    }
}
