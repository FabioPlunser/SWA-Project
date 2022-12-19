package at.ac.uibk.swa.config;

import at.ac.uibk.swa.models.Person;
import lombok.Getter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.SpringSecurityCoreVersion;

import java.io.Serial;
import java.util.Optional;
import java.util.UUID;

@Getter
public class PersonAuthenticationToken extends UsernamePasswordAuthenticationToken {

    @Serial
    private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;

    private Optional<Person> person = Optional.empty();
    private final UUID token;

    public PersonAuthenticationToken(UUID token) {
        super(null, token);
        this.token = token;
    }

    public void setDetails(Person person) {
        super.setDetails(person);
        this.person = Optional.ofNullable(person);
    }
}
