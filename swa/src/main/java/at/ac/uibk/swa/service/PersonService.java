package at.ac.uibk.swa.service;

import at.ac.uibk.swa.models.Permission;
import at.ac.uibk.swa.models.Person;
import at.ac.uibk.swa.repositories.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;

@Service("personService")
public class PersonService {

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<Person> getPersons() {
        return personRepository.findAll();
    }

    public Optional<Person> login(String username, String password) {
        Optional<Person> maybePerson = personRepository.findByUsername(username);
        if(maybePerson.isEmpty())
            return Optional.empty();

        Person person = maybePerson.get();
        if(!passwordEncoder.matches(password, person.getPasswdHash()))
            return Optional.empty();

        UUID token = UUID.randomUUID();
        person.setToken(token);
        // NOTE: Person.Token has a unique-Constraint
        // => if the same Token is generated for multiple users, the save fails
        try {
            personRepository.save(person);
        } catch (Exception e) {
            return Optional.empty();
        }

        return Optional.of(person);
    }

    public Optional<Person> findByToken(UUID token) {
        // TODO: Should this also get a Username and check if the Token is associated with the given username?
        //       Theoretically not needed because the Token has a unique Constraint
        //       but would make it even harder to brute force for a Token as you would need to guess the username
        //       and the Token at the same time.
        // NOTE: Maybe switch to JWT? UUID is OK, but definitely not the best solution

        return Optional.of(token)
                .map(personRepository::findByToken)
                .flatMap(Function.identity());
    }
    
    public Optional<Person> findById(UUID id) {
        return personRepository.findById(id);
    }

    /**
     * Logout a user
     *
     * @param person user to be logged out
     * @return true if user has been logged out, false otherwise
     */
    public boolean logout(Person person) {
        if (person != null && person.getPersonId() != null && person.getToken() != null) {
            person.setToken(null);
            return save(person) != null;
        } else {
            return false;
        }
    }

    /**
     * Logout a user
     * @param token token of the user to be logged out
     * @return true if user has been logged out, false otherwise
     */
    public boolean logout(UUID token) {
        Optional<Person> maybePerson = personRepository.findByToken(token);
        return maybePerson.filter(this::logout).isPresent();
    }

    /**
     * Creates a new person in the repository
     *
     * @param person person to be created
     * @return true if person has been created, false otherwise
     */
    public boolean create(Person person) {
        if (person != null && person.getPersonId() == null) {
            // person is created with password in plain text
            // encode to hash before proceeding
            String password = person.getPasswdHash();
            person.setPasswdHash(passwordEncoder.encode(password));

            // save the person
            boolean success = (save(person) != null);

            // reset password to original one so hash cannot be
            person.setPasswdHash(password);

            return success;
        } else {
            return false;
        }
    }

    /**
     * Saves a person to the repository
     *
     * @param person person to save
     * @return person that has been saved if successfull, null otherwise
     */
    private Person save(Person person) {
        try {
            return personRepository.save(person);
        } catch (Exception e) {
            return null;
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
     * @param permissions The new List of Permissions.
     * @param password The new Password.
     * @return true if the user could be found and could be updated, false otherwise.
     */
    // TODO: Maybe add email address
    public boolean update(UUID personId, String username, Set<Permission> permissions, String password) {
        Optional<Person> maybePerson = personRepository.findById(personId);
        if(maybePerson.isPresent()) {
            Person person = maybePerson.get();

            if (username    != null) person.setUsername(username);
            if (permissions != null) person.setPermissions(permissions);
            if (password    != null) person.setPasswdHash(passwordEncoder.encode(password));

            boolean success = save(person) != null;

            if (password != null) person.setPasswdHash(password);

            return success;
        }

        return false;
    }

    public boolean delete(UUID personId) {
        try {
            this.personRepository.deleteById(personId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
