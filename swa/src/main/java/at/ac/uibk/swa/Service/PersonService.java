package at.ac.uibk.swa.Service;

import at.ac.uibk.swa.Models.Permission;
import at.ac.uibk.swa.Models.Person;
import at.ac.uibk.swa.Repositories.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

@Service("personService")
public class PersonService {

    @Autowired
    PersonRepository personRepository;

    public List<Person> getPersons() {
        return personRepository.findAll();
    }

    public Optional<UUID> login(String username, String password) {
        Optional<Person> maybePerson = personRepository.findByUsernameAndPasswdHash(username,password);
        if(maybePerson.isPresent()){
            UUID token = UUID.randomUUID();
            Person person = maybePerson.get();
            person.setToken(token);
            // TODO: Token has a unique Constraint => save may fail if the same Token is generated
            //       FIX: Create Loop? Can the Database generate the Token?
            personRepository.save(person);
            return Optional.of(token);
        }

        return Optional.empty();
    }

    public Optional<Person> findByToken(UUID token) {
        // TODO: Should this also get a Username and check if the Token is associated with the given username?
        //       Theoretically not needed because the Token has a unique Constraint
        //       but would make it even harder to brute force for a Token as you would need to guess the username
        //       and the Token at the same time.

        return Optional.of(token)
                .map(personRepository::findByToken)
                .flatMap(Function.identity());
    }

    public Optional<Person> findById(UUID id) {
        return personRepository.findById(id);
    }

    public boolean logout(UUID token) {
        Optional<Person> maybePerson = personRepository.findByToken(token);
        if(maybePerson.isPresent()){
            Person person = maybePerson.get();
            // Delete the Token on Logout
            person.setToken(null);
            personRepository.save(person);
            return true;
        }
        return false;
    }

    public boolean save(Person person) {
        try {
            // NOTE: This save may fail if the usernames are equal because username has a unique Constraint
            //       => See Customer.username
            this.personRepository.save(person);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean delete(UUID personId) {
        try {
            this.personRepository.deleteById(personId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Updates a Person with the Values given as Parameters.
     * The User is retrieved using the personId.
     * The other Parameters are used to change the user.
     * Parameters that are set to null are left unchanged.
     *
     * @param personId The ID of the User to change.
     * @param username The new Username.
     * @param email The new Email
     * @param permissions The new List of Permissions.
     * @param password The new Password.
     * @return true if the user could be found and could be updated, false otherwise.
     */
    public boolean update(UUID personId, String username, String email, List<Permission> permissions, String password) {
        Optional<Person> maybePerson = personRepository.findById(personId);
        if(maybePerson.isPresent()) {
            Person person = maybePerson.get();

            if (username    != null) person.setUsername(username);
            if (email       != null) person.setEmail(email);
            if (permissions != null) person.setPermissions(permissions);
            if (password    != null) person.setPasswdHash(password);

            return save(person);
        }

        return false;
    }
}
