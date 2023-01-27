package at.ac.uibk.swa.service;

import at.ac.uibk.swa.models.Deck;
import at.ac.uibk.swa.models.Person;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service("mailService")
public class MailService {

    @Autowired
    private JavaMailSender mailSender;

    /**
     * Notifies all subscribers and the creator of a deck per email if the deck is blocked by admin.
     * @param deck
     */
    public void notifyBlockedDeck(Deck deck) {
        String[] recipients = deck.getSubscribedPersons().stream().map(Person::getEmail).toArray(String[]::new);
        sendMessage(String.format("Dear User, %nDeck \"%s\" does not comply with our policies and has been blocked!",
                deck.getName()),
                "Deck blocked",
                recipients);
    }

    /**
     * Notifies all subscribers and the creator of a deck per email if the deck is unblocked by admin.
     * @param deck
     */
    public void notifyUnblockedDeck(Deck deck) {
        String[] recipients = deck.getSubscribedPersons().stream().map(Person::getEmail).toArray(String[]::new);
        sendMessage(String.format("Dear User, %nDeck \"%s\" complies with our policies and has been unblocked!",
                deck.getName()),
                "Deck unblocked",
                recipients);
    }

    /**
     * Sends an email to one or several people through the address "simpson-lisa@gmx.at".
     * Recipients cannot see whether other people received the message too or who they would be
     * @param text the content of the mail as String
     * @param subject the subject of the mail as String
     * @param recipients String array of email addresses of everyone who should receive the message
     */
    public void sendMessage(String text, String subject, String[] recipients) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("simpson-lisa@gmx.at");
        message.setBcc(recipients);
        message.setSubject(subject);
        message.setText(text);

        try {
            mailSender.send(message);
        } catch (MailSendException e) {
            log.warn("Unable to send all mails - user email probably not validated");
        }
    }
}