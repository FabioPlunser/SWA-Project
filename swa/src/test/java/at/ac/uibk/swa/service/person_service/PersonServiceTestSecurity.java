package at.ac.uibk.swa.service.person_service;

import at.ac.uibk.swa.models.Person;
import at.ac.uibk.swa.service.PersonService;
import at.ac.uibk.swa.util.StringGenerator;
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
public class PersonServiceTestSecurity {
    @Autowired
    private PersonService personService;

    @Test
    public void testLoginWithValidCredentials() {
        // given: demo user in database (and additional anonymous user)
        int numberOfOtherPersons = 20;
        String username = "person-TestLoginWithValidCredentials";
        String password = StringGenerator.password();
        Person person = new Person(username, StringGenerator.email(), password, Set.of());
        assertTrue(personService.create(person), "Unable to create user " + person);
        for (int i = 0; i < numberOfOtherPersons; i++) {
            assertTrue(personService.create(new Person(
                    "otherPerson-TestLoginWithValidCredentials-" + (i+1),
                    StringGenerator.email(),
                    StringGenerator.password(),
                    Set.of()
            )), "Unable to create user " + person);
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
        assertTrue(personService.create(person), "Unable to create user " + person);

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
        assertTrue(personService.create(person), "Unable to create user for test");

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
    public void testLogout() {
        // given: demo user in database, logged in
        String username = "person-TestLogout";
        String password = StringGenerator.password();
        Person person = new Person(username, StringGenerator.email(), password, Set.of());
        assertTrue(personService.create(person), "Unable to create user for test");
        Optional<Person> maybePerson = personService.login(username, password);
        assertTrue(maybePerson.isPresent(), "Could not login");
        Person loggedInPerson = maybePerson.get();
        UUID token = loggedInPerson.getToken();

        // when: logging out
        assertTrue(personService.logout(loggedInPerson), "Could not log out");

        // then: retrieving user by token should not be possible anymore
        Optional<Person> maybeLoggedOutPerson = personService.findByToken(token);
        assertTrue(maybeLoggedOutPerson.isEmpty(), "Token still valid after logout");
    }

    @Test
    public void testLogoutWithToken() {
        // given: demo user in database, logged in
        String username = "person-testLogoutWithToken";
        String password = StringGenerator.password();
        Person person = new Person(username, StringGenerator.email(), password, Set.of());
        assertTrue(personService.create(person), "Unable to create user for test");
        Optional<Person> maybePerson = personService.login(username, password);
        assertTrue(maybePerson.isPresent(), "Could not login");
        UUID token = maybePerson.get().getToken();

        // when: logging out with token directly
        assertTrue(personService.logout(token), "Could not log out");

        // then: retrieving user by token should not be possible anymore
        Optional<Person> maybeLoggedOutPerson = personService.findByToken(token);
        assertTrue(maybeLoggedOutPerson.isEmpty(), "Token still valid after logout");
    }
}
