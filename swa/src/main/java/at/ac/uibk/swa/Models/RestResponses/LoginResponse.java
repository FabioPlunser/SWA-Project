package at.ac.uibk.swa.Models.RestResponses;

import at.ac.uibk.swa.Models.Authenticable;
import at.ac.uibk.swa.Models.Permission;
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
public class LoginResponse extends TokenResponse implements Serializable {

    private UUID personId;
    private List<Permission> permissions;

    public LoginResponse(Authenticable authenticable) {
        super(true, authenticable.getToken());
        this.personId = authenticable.getId();
        this.permissions = authenticable.getPermissions();
    }
}
