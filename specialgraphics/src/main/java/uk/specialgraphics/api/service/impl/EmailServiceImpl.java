package uk.specialgraphics.api.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import uk.specialgraphics.api.payload.response.SuccessResponse;
import uk.specialgraphics.api.service.EmailService;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Override
    public void sendSimpleEmail(String toEmail, String subject, String body) {

        MimeMessage message = mailSender.createMimeMessage();

        MimeMessageHelper helper = null;
        try {
            helper = new MimeMessageHelper(message, true);
            helper.setFrom("info@specialgraphics.us");
            helper.setTo(toEmail);
            helper.setSubject(subject);


            helper.setText(body, true);



            // Send the email
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }


    }
}
