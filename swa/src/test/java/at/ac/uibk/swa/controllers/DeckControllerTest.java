package at.ac.uibk.swa.controllers;

import at.ac.uibk.swa.models.Permission;
import at.ac.uibk.swa.models.Person;
import at.ac.uibk.swa.service.PersonService;
import at.ac.uibk.swa.util.EndpointMatcherUtil;
import at.ac.uibk.swa.util.StringGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class DeckControllerTest {
    @Autowired
    private PersonService personService;
    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    private MockMvc mockMvc;    // ignore if error shown by intellij

    private String createUserAndGetToken() throws Exception {
        String username = StringGenerator.username();
        String password = StringGenerator.password();
        Set<Permission> permissions = Set.of(Permission.USER);
        Person person = new Person(username, StringGenerator.email(), password, permissions);
        assertTrue(personService.create(person), "Unable to create user");
        Optional<Person> maybePerson = personService.login(username, password);
        assertTrue(maybePerson.isPresent(), "Unable to login");
        return "Bearer " + maybePerson.get().getToken().toString();
    }

    @Test
    void createDeck() throws Exception {
        // given: user created in database, logged in
        String token = createUserAndGetToken();

        // when: creating a new deck
        MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders
                .put("/api/createDeck")
                        .header("Authorization", token)
                        .param("name", "test")
                        .param("description", "test")
                .contentType(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse();

        System.out.println(response.getContentAsString());
    }
}