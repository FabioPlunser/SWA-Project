package at.ac.uibk.swa.config;

import at.ac.uibk.swa.models.Permission;
import at.ac.uibk.swa.models.Person;
import at.ac.uibk.swa.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

import java.util.UUID;

@Configuration
public class StartupConfig {
    @Autowired
    private PersonService personService;

    /**
     * Injected Name of the Active Profile specified in the Application Properties.
     */
    @Value("${spring.profiles.active}")
    private String activeProfileString;
    private Profile activeProfile;

    /**
     * Injected Name of the Database Driver specified in the Application Properties.
     */
    @Value("${spring.datasource.driver-class-name}")
    private String dbDriver;

    /**
     * The In-Memory Database Driver used for testing and in development.
     */
    private Class testDbDriver = org.h2.Driver.class;

    @EventListener(ApplicationReadyEvent.class)
    public void createBaseAdminUser() {
        switch (activeProfile) {
            case DEV -> {
                if (dbDriver.equals(testDbDriver.getName())) {
                    this.personService.create(new Person(
                            "Admin", "admin@noreply.com", "password",
                            UUID.fromString("62b3e09e-c529-40c6-85c6-1afc53e17408"),
                            Permission.allAuthorities()
                    ));
                }
            }
            default -> {}
        }
    }

    /**
     * Helper Class for easier Handling of the possible Profiles.
     */
    private enum Profile {
        DEV,
        PROD,
        TEST,
        OTHER;

        public static Profile fromString(String string) {
            try {
                return Profile.valueOf(string.toUpperCase());
            } catch (Exception e) {
                return Profile.OTHER;
            }
        }
    }
}
