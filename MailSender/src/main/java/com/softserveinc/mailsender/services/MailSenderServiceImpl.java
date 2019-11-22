package com.softserveinc.mailsender.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.Arrays;

@Service
public class MailSenderServiceImpl implements MailSenderService {
    private static final Logger log= LoggerFactory.getLogger(MailSenderServiceImpl.class);

    @Override
    public void sendMail(MimeMessage message) {
        try{
            log.info("Sending mail to "+ Arrays.toString(message.getAllRecipients()));

            Transport.send(message);
        } catch (AddressException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
