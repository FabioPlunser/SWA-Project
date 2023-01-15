package at.ac.uibk.swa.service.mail_service;

import at.ac.uibk.swa.models.Deck;
import at.ac.uibk.swa.service.MailService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public class TestMailServiceMock {

    @MockBean
    MailService mockMailService;
    @Autowired
    private MailService mailService;

    @Test
    public void TestSendMessageMock() {
        Mockito.doNothing()
                .when(mockMailService)
                .sendMessage(Mockito.anyString(), Mockito.anyString(), Mockito.any(String[].class));

        mailService.sendMessage("Test text", "Test subject", new String[]{"test@subject.com"});

        Mockito.verify(mockMailService, Mockito.times(1)).sendMessage("Test text", "Test subject", new String[]{"test@subject.com"});  // verify mock has been called 1 time
    }

    @Test
    public void TestNotifyBlockedDeckMock() {
        Deck deck = new Deck();
        Mockito.doNothing()
                .when(mockMailService)
                .notifyBlockedDeck(deck);

        mailService.notifyBlockedDeck(deck);

        Mockito.verify(mockMailService, Mockito.times(1)).notifyBlockedDeck(deck);
    }

    @Test
    public void TestNotifyUnblockedDeckMock() {
        Deck deck = new Deck();
        Mockito.doNothing()
                .when(mockMailService)
                .notifyUnblockedDeck(deck);

        mailService.notifyUnblockedDeck(deck);

        Mockito.verify(mockMailService, Mockito.times(1)).notifyUnblockedDeck(deck);
    }
}
