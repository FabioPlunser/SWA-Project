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

    @Autowired
    private MailConfiguration mailConfiguration;
    @Autowired
    private JavaMailSender mailSender;

    public void notifyBlockedDeck(Deck deck) {
        String[] recipients = deck.getSubscribedPersons().stream().map(Person::getEmail).toArray(String[]::new);
        sendMessage(String.format("Deck \"%s\" has been blocked!", deck.getName()), "Deck blocked", recipients);
    }

    public void sendMessage(String text, String subject, String[] recipients) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("simpson-lisa@gmx.at");
        message.setTo(recipients);
        message.setSubject(subject);
        message.setText(text);

        mailSender.send(message);
    }
}