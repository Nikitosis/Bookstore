package com.services;

import com.MainConfig;
import com.crossapi.models.Mail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

@Service
public class MailSenderServiceImpl implements MailSenderService {
    private MainConfig mainConfig;
    private Session session;

    @Autowired
    public MailSenderServiceImpl(MainConfig mainConfig, Session session) {
        this.mainConfig = mainConfig;
        this.session = session;
    }

    @Override
    public void sendMail(Mail mail) {
        MimeMessage message=new MimeMessage(session);

        try{
            message.setFrom(new InternetAddress(mainConfig.getMailConfig().getFromAddress()));
            message.setRecipients(
                    Message.RecipientType.TO,
                    mail.getReceiverEmail()
                    );
            message.setSubject(mail.getSubject());
            message.setText(mail.getBody());

            Transport.send(message);
        } catch (AddressException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
