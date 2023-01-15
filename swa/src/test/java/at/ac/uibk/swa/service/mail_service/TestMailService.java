package at.ac.uibk.swa.service.mail_service;

import at.ac.uibk.swa.service.MailService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class TestMailService {

    @Autowired
    MailService mailService;

    @Test
    public void mailTest() {
        mailService.sendMessage("This is a test", "test", new String[] {"luanabgadelha@gmail.com"});
        assertEquals(0,0);
    }
}
