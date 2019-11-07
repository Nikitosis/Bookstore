package com.softserveinc.mailsender.services;

import com.softserveinc.mailsender.MainConfig;
import com.softserveinc.cross_api_objects.models.Mail;
import com.softserveinc.mailsender.resources.MailSenderResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.URLDataSource;
import javax.mail.*;
import javax.mail.internet.*;
import java.net.MalformedURLException;
import java.net.URL;

@Service
public class MailSenderServiceImpl implements MailSenderService {
    private MainConfig mainConfig;
    private Session session;

    private static final Logger log= LoggerFactory.getLogger(MailSenderResource.class);

    @Autowired
    public MailSenderServiceImpl(MainConfig mainConfig, Session session) {
        this.mainConfig = mainConfig;
        this.session = session;
    }

    @Override
    public void sendMail(Mail mail) {
        MimeMessage message=new MimeMessage(session);

        try{
            log.info("Sending mail to "+mail.getReceiverEmail());

            message.setFrom(new InternetAddress(mainConfig.getMailConfig().getFromAddress()));
            message.setRecipients(
                    Message.RecipientType.TO,
                    mail.getReceiverEmail()
                    );
            message.setSubject(mail.getSubject());

            //create whole body
            Multipart multipart=new MimeMultipart();

            //create body text
            MimeBodyPart textBodyPart=new MimeBodyPart();
            textBodyPart.setText(mail.getBody());

            multipart.addBodyPart(textBodyPart);

            //add attachment
            if(mail.getAttachment()!=null){
                URL fileUrl=new URL(mail.getAttachment().getAttachmentUrl());
                DataSource dataSource=new URLDataSource(fileUrl);
                BodyPart filePart=new MimeBodyPart();
                filePart.setDataHandler(new DataHandler(dataSource));
                filePart.setFileName(mail.getAttachment().getAttachmentName());

                multipart.addBodyPart(filePart);
            }

            message.setContent(multipart);

            //send message
            Transport.send(message);
        } catch (AddressException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }
}
