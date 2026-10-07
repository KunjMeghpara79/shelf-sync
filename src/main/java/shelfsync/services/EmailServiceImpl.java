package shelfsync.services;

import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import shelfsync.models.entities.EmailDetails;
import shelfsync.services.interfaces.EmailService;

@Service
public class EmailServiceImpl implements EmailService {
    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String sender;

    @Override
    public String sendSimpleMail(EmailDetails details) {
        try {

            MimeMessage message = javaMailSender.createMimeMessage();

            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(sender);
            helper.setTo(details.getRecipient());
            helper.setSubject(details.getSubject());

            // true = HTML email
            helper.setText(details.getMsgBody(), true);

            javaMailSender.send(message);

            return "Mail Sent Successfully";

        } catch (Exception e) {
            e.printStackTrace();
            return "Error while sending mail: " + e.getMessage();
        }
    }
}
