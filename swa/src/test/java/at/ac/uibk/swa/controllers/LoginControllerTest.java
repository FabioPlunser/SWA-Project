package at.ac.uibk.swa.controllers;

import at.ac.uibk.swa.models.Permission;
import at.ac.uibk.swa.models.Person;
import at.ac.uibk.swa.service.PersonService;
import at.ac.uibk.swa.util.EndpointMatcherUtil;
import at.ac.uibk.swa.util.StringGenerator;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.event.annotation.BeforeTestExecution;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class LoginControllerTest {
    @Autowired
    private PersonService personService;
    @Autowired
    private MockMvc mockMvc;    // ignore if error shown by intellij


    @Test
    public void testLogin() throws Exception {
        // given: user created in database
        String username = "user-testLogin";
        String password = StringGenerator.password();
        Set<Permission> permissions = Set.of(Permission.USER, Permission.ADMIN);
        personService.create(new Person(username, StringGenerator.email(), password, permissions));

        // when: logging in as that user
        // then: status code 200 must be returned and token must be in body
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .post(EndpointMatcherUtil.loginEndpoint)
                .param("username", username)
                .param("password", password)
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpectAll(
                status().isOk(),
                jsonPath("$.token").exists(),
                jsonPath("$.permissions").isArray()
                // TODO: check contents of permissions
        );
    }

    @Test
    public void testLoginInvalidCredentials() throws Exception {

    }
}