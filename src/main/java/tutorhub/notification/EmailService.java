package tutorhub.notification;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Instant;

/**
 * Sends plain-text emails. @Async runs each send on a background thread so the
 * caller (the reminder job, or any request) is never blocked by SMTP latency.
 *
 * @Async only works across beans — it's a proxy, so a different bean must call
 * this method (the reminder job does).
 */
@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final String from;

    public EmailService(JavaMailSender mailSender,
                        @Value("${tutorhub.mail.from:noreply@tutorhub.local}") String from) {
        this.mailSender = mailSender;
        this.from = from;
    }

    @Async
    public void sendAssignmentReminder(String to, String studentName, String assignmentTitle, Instant dueDate) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject("Reminder: \"" + assignmentTitle + "\" is due soon");
        message.setText("""
                Hi %s,

                This is a reminder that your assignment "%s" is due on %s.

                Good luck!
                TutorHub
                """.formatted(studentName, assignmentTitle, dueDate));
        mailSender.send(message);
    }
}