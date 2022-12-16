package at.ac.uibk.swa.Config;

import at.ac.uibk.swa.Models.Person;
import lombok.Getter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.SpringSecurityCoreVersion;

import java.util.Optional;
import java.util.UUID;

@Getter
public class PersonAuthenticationToken extends UsernamePasswordAuthenticationToken {

    private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;

    private Optional<Person> person = Optional.empty();
    private final UUID token;

    public PersonAuthenticationToken(UUID token) {
        super(null, token);
        this.token = token;
    }

    public void setDetails(Person person) {
        super.setDetails(person);
        this.person = Optional.of(person);
    }
}
