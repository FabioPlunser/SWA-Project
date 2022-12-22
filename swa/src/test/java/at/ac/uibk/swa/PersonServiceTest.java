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

import javax.print.DocFlavor;
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

        // then: all saved users must be found again, attributes must be identical and no additional users must be returned
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
        // given: demo user in database (and additional anonymous user)
        int numberOfOtherPersons = 20;
        String username = "person-TestLoginWithValidCredentials";
        String password = StringGenerator.password();
        Person person = new Person(username, StringGenerator.email(), password, Set.of());
        assertTrue(personService.save(person), "Unable to save user " + person);
        for (int i = 0; i < numberOfOtherPersons; i++) {
            assertTrue(personService.save(new Person(
                    "otherPerson-TestLoginWithValidCredentials-" + (i+1),
                    StringGenerator.email(),
                    StringGenerator.password(),
                    Set.of()
            )), "Unable to save user " + person);
        }

        // when: logging in with that users credentials
        Optional<Person> maybePerson = personService.login(username, password);
        assertTrue(maybePerson.isPresent(), "Unable to log in");

        // then: returned user must be correct
        assertEquals(person, maybePerson.get(), "Got the wrong user");
    }

    @Test
    public void testLoginWithInvalidCredentials() {
        // given: demo user in database
        int numberOfOtherPersons = 20;
        String username = "person-TestLoginWithInvalidCredentials";
        String password = "password";
        Person person = new Person(username, StringGenerator.email(), password, Set.of());
        assertTrue(personService.save(person), "Unable to save user " + person);

        // when:
        //  logging in with completely wrong credentials
        Optional<Person> maybePersonAllWrong = personService.login("wrong-username", "wrong-password");
        //  logging in with wrong username
        Optional<Person> maybePersonUsernameWrong = personService.login("wrong-username", password);
        //  logging in with wrong password
        Optional<Person> maybePersonPasswordWrong = personService.login(username, "wrong-password");

        // then: login should never be possible
        assertTrue(maybePersonAllWrong.isEmpty(), "Could login with completely different credentials");
        assertTrue(maybePersonUsernameWrong.isEmpty(), "Could login with wrong username");
        assertTrue(maybePersonPasswordWrong.isEmpty(), "Could login with wrong password");
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
