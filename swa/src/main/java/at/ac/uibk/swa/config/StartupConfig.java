package at.ac.uibk.swa.config;

import at.ac.uibk.swa.models.Permission;
import at.ac.uibk.swa.models.Person;
import at.ac.uibk.swa.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Configuration
public class StartupConfig {
    @Autowired
    private PersonService personService;

    @EventListener(ApplicationReadyEvent.class)
    public void createBaseAdminUser() {
        this.personService.create(new Person(
                "Admin", "admin@noreply.com", "password",
                UUID.fromString("62b3e09e-c529-40c6-85c6-1afc53e17408"), Permission.allAuthorities()
        ));
    }
}
