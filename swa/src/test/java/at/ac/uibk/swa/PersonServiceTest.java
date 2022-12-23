package at.ac.uibk.swa;

import at.ac.uibk.swa.models.Permission;
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

import static org.junit.jupiter.api.Assertions.*;

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

        // then: all saved users must be found again and attributes must be identical
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
        String username = "person-TestGetPersonByToken";
        String password = StringGenerator.password();
        Person person = new Person(username, StringGenerator.email(), password, Set.of());
        assertTrue(personService.save(person), "Unable to save user for test");

        // when: logging in as user and retrieving token
        Optional<Person> maybePerson = personService.login(username, password);
        assertTrue(maybePerson.isPresent(), "Could not login");
        UUID token = maybePerson.get().getToken();

        // then: user returned by handing over token must be original user
        Optional<Person> maybePersonByToken = personService.findByToken(token);
        assertTrue(maybePersonByToken.isPresent(), "Did not find user by token");
        assertEquals(person, maybePersonByToken.get(), "Got user " + maybePersonByToken.get() + " when user " + person + " was expected");
    }

    @Test
    public void testGetPersonById() {
        // given: demo user in database
        Person person = new Person("person-TestGetPersonById", StringGenerator.email(), StringGenerator.password(), Set.of());
        assertTrue(personService.save(person), "Unable to save user for test");
        UUID id = person.getPersonId();

        // when: retrieving user from database by id
        Optional<Person> maybePerson = personService.findById(id);

        // then: retrieved user must be correct
        assertTrue(maybePerson.isPresent(), "Did not find user by id");
        assertEquals(person, maybePerson.get(), "Got user " + maybePerson.get() + " when user " + person + " was expected");
    }

    @Test
    public void testLogout() {
        // given: demo user in database, logged in
        String username = "person-TestLogout";
        String password = StringGenerator.password();
        Person person = new Person(username, StringGenerator.email(), password, Set.of());
        assertTrue(personService.save(person), "Unable to save user for test");
        Optional<Person> maybePerson = personService.login(username, password);
        assertTrue(maybePerson.isPresent(), "Could not login");
        UUID token = maybePerson.get().getToken();

        // when: logging out
        assertTrue(personService.logout(token), "Could not log out");

        // then: retrieving user by token should not be possible anymore
        Optional<Person> maybeLoggedOutPerson = personService.findByToken(token);
        assertTrue(maybeLoggedOutPerson.isEmpty(), "Token still valid after logout");
    }

    @Test
    public void testUpdatePerson() {
        // given: demo user in database
        String username = "person-TestUpdatePerson";
        String password = StringGenerator.password();
        String email = StringGenerator.email();
        Person person = new Person(username, email, password, Set.of());
        assertTrue(personService.save(person), "Unable to save user for test");

        // when: updating the person
        String newUsername = "person-TestUpdatePerson-new";
        String newPassword = StringGenerator.password();
        Set<Permission> newPermissions = Set.of(Permission.ADMIN, Permission.USER);
        assertTrue(personService.update(person.getPersonId(), newUsername, newPermissions, newPassword), "Could not update user");

        // then: logging in should be possible with new credentials only and other attributes must be correct
        Optional<Person> maybePerson = personService.login(newUsername, newPassword);
        assertTrue(maybePerson.isPresent(), "Could not login with new credentials");
        Optional<Person> maybeOldCredentialsPerson = personService.login(username, password);
        assertTrue(maybeOldCredentialsPerson.isEmpty(), "Could still login with old credentials");
        assertEquals(newPermissions, maybePerson.get().getPermissions(), "Permissions have not been updated");
    }

    @Test
    public void testDeletePerson() {
        // given: demo user in database
        Person person = new Person("person-TestDeletePerson", StringGenerator.email(), StringGenerator.password(), Set.of());
        assertTrue(personService.save(person), "Unable to save user for test");

        // when: deleting that user
        assertTrue(personService.delete(person.getPersonId()), "Could not delete user");

        // then: user should not exist in database anymore
        assertFalse(personService.getPersons().contains(person), "User still in database");
    }
}
