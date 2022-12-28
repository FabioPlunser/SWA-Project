package at.ac.uibk.swa.service;

import at.ac.uibk.swa.config.personAuthentication.AuthContext;
import at.ac.uibk.swa.models.Authenticable;
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

    /**
     * Gets a list of all persons in the repository
     *
     * @return list of found persons
     */
    public List<Person> getPersons() {
        return personRepository.findAll();
    }

    /**
     * Login via username and password
     *
     * @param username username of the person to be logged in
     * @param password password of the person to be logged in
     * @return person if successfully logged in, nothing otherwise
     */
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

    /**
     * Find a person via its current token
     *
     * @param token current token of the person to be found
     * @return person if found, otherwise nothing
     */
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

    /**
     * Find a person with its id
     *
     * @param id id of the person to be found
     * @return person if found, otherwise nothing
     */
    public Optional<Person> findById(UUID id) {
        return personRepository.findById(id);
    }

    /**
     * Logout a user
     * TODO: Required? Maybe from admin side?
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
     * TODO: Required?
     *
     * @param token token of the user to be logged out
     * @return true if user has been logged out, false otherwise
     */
    public boolean logout(UUID token) {
        Optional<Person> maybePerson = personRepository.findByToken(token);
        return maybePerson.filter(this::logout).isPresent();
    }

    /**
     * Logout the currently logged in user
     *
     * @return true if user has been logged out, false otherwise
     */
    public boolean logout() {
        Optional<Authenticable> maybeUser = AuthContext.getCurrentUser();
        if (maybeUser.isPresent() && maybeUser.get() instanceof Person person) {
            return logout(person);
        } else {
            return false;
        }
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
     * Updates a Person with the values given as Parameters.
     * The User is given directly
     * The other Parameters are used to change the user.
     * Parameters that are set to null are left unchanged.
     *
     * @param person The person to update
     * @param username The new Username.
     * @param permissions The set of new permissions.
     * @param password The new Password.
     * @return true if the user could be found and could be updated, false otherwise.
     */
    // TODO: Maybe add email address
    public boolean update(Person person, String username, Set<Permission> permissions, String password) {
        if(person != null && person.getPersonId() != null) {
            if (username    != null) person.setUsername(username);
            if (permissions != null) person.setPermissions(permissions);
            if (password    != null) person.setPasswdHash(passwordEncoder.encode(password));

            boolean success = save(person) != null;

            if (password != null) person.setPasswdHash(password);

            return success;
        }

        return false;
    }

    /**
     * updates a person with the values given as parameters
     * person is identified by id
     * NOTE: this is an admin function!
     *
     * @param personId id of the person to update
     * @param username new username
     * @param permissions set of new permissions
     * @param password new password
     * @return true if user was successfully update, false otherwise
     */
    public boolean update(UUID personId, String username, Set<Permission> permissions, String password) {
        Optional<Person> maybePerson = findById(personId);
        return maybePerson.filter(person -> update(person, username, permissions, password)).isPresent();
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
