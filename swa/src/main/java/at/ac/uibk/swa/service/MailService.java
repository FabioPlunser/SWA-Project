package at.ac.uibk.swa.service;

import at.ac.uibk.swa.models.Deck;
import at.ac.uibk.swa.models.Person;
import at.ac.uibk.swa.config.MailConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service("mailService")
public class MailService {

    // TODO See: https://mailtrap.io/blog/spring-send-email/
    //           https://www.baeldung.com/spring-email

    @Autowired
    private MailConfiguration mailConfiguration;

    public void notifyBlockedDeck(Deck deck) {
        JavaMailSender mailSender = mailConfiguration.getJavaMailSender();
        String[] recipients = deck.getSubscribedPersons().stream().map(Person::getEmail).toArray(String[]::new);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(recipients);
        message.setSubject(String.format("Deck \"%s\" has been blocked!", deck.getName()));
        message.setText("");

        mailSender.send(message);
    }
}