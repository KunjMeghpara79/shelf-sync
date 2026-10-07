package shelfsync.services.interfaces;

import shelfsync.models.entities.EmailDetails;

public interface EmailService {
    String sendSimpleMail(EmailDetails details);
}
