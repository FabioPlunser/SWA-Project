package at.ac.uibk.swa;

import at.ac.uibk.swa.models.Person;
import at.ac.uibk.swa.service.CardService;
import at.ac.uibk.swa.service.DeckService;
import at.ac.uibk.swa.service.PersonService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
public class PersonServiceTest {
    @Autowired
    private DeckService deckService;
    @Autowired
    private CardService cardService;
    @Autowired
    private PersonService personService;

    @Test
    public void testRetrieveUserFromToken() {
        // given: demo user in database
        Person person = new Person("person-TestRetrieveUserFromToken", "", "", Set.of());
        assertTrue(personService.save(person), "Unable to save user for test");

        // when: logging in as user and retrieving token
        Optional<Person> maybePerson = personService.login(person.getUsername(), person.getPasswdHash());
        assertTrue(maybePerson.isPresent(), "Could not login user");
        UUID token = maybePerson.get().getToken();

        // then: user returned by handing over token must be original user
        Optional<Person> oPerson = personService.findByToken(token);
        assertTrue(oPerson.isPresent(), "Did not find user by token");
        assertEquals(person.getPersonId(), oPerson.get().getPersonId(), "Got user " + oPerson.get().getPersonId() + " when user " + person.getPersonId() + " was expected");
    }
}
