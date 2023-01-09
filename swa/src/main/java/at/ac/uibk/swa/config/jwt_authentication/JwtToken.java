package at.ac.uibk.swa.config.jwt_authentication;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class JwtToken {
    private String username;
    private UUID token;
}