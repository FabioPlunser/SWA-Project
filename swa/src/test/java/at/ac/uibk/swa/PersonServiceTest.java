package at.ac.uibk.swa;

import at.ac.uibk.swa.models.Person;
import at.ac.uibk.swa.service.CardService;
import at.ac.uibk.swa.service.DeckService;
import at.ac.uibk.swa.service.PersonService;
import at.ac.uibk.swa.util.StringGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.*;

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
    public void testSaveAndGetPersons() {
        // given: some demo users stored in database
        int numberOfDemoPersons = 20;
        List<Person> savedPersons = new ArrayList<>();
        for (int i = 0; i < numberOfDemoPersons; i++) {
            Person person = new Person(
                    "person-TestSaveAndGetPersons-" + (i+1),
                    StringGenerator.email(),
                    StringGenerator.password(),
                    Set.of()
            );
            savedPersons.add(person);
            assertTrue(personService.save(person), "Unable to save user " + person);
        }

        // when: retrieving all demo users from database
        List<Person> foundPersons = personService.getPersons();

        // then: all saved persons must be found again, attributes must be identical and no additional persons must be returned
        assertEquals(savedPersons.size(), foundPersons.size(), "Expected " + savedPersons.size() + " but found " + foundPersons.size());
        for (Person person: savedPersons) {
            assertTrue(foundPersons.contains(person), "Could not find person " + person);
            Person foundPerson = foundPersons.get(foundPersons.indexOf(person));
            assertEquals(person.getPersonId(), foundPerson.getPersonId(), "Wrong id of " + person);
            assertEquals(person.getUsername(), foundPerson.getUsername(), "Wrong username of " + person);
            assertEquals(person.getEmail(), foundPerson.getEmail(), "Wrong email of " + person);
            assertEquals(person.getPermissions(), foundPerson.getPermissions(), "Wrong permissions of " + person);
        }
    }

    @Test
    public void testLoginWithValidCredentials() {
        // given: demo user in database
        String password = StringGenerator.password();
        Person person = new Person("person-TestLoginWithValidCredetials", StringGenerator.email(), password, Set.of());
    }

    @Test
    public void testLoginWithInvalidCredentials() {
        //TODO
    }

    @Test
    public void testGetPersonByToken() {
        // given: demo user in database
        Person person = new Person("person-TestGetPersonByToken", "", "", Set.of());
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

    @Test
    public void testGetPersonById() {
        //TODO
    }

    @Test
    public void testLogout() {
        //TODO
    }

    @Test
    public void testUpdatePerson() {
        //TODO
    }

    @Test
    public void testDeletePerson() {
        //TODO
    }
}
