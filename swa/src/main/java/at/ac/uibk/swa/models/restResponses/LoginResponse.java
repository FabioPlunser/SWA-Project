package at.ac.uibk.swa.models.restResponses;

import at.ac.uibk.swa.models.Authenticable;
import at.ac.uibk.swa.models.Permission;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Set;
import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.MODULE)
@AllArgsConstructor
public class LoginResponse extends TokenResponse implements Serializable {

    private UUID personId;
    private Set<Permission> permissions;

    public LoginResponse(Authenticable authenticable) {
        super(true, authenticable.getToken());
        this.personId = authenticable.getId();
        this.permissions = authenticable.getPermissions();
    }
}
