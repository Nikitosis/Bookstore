package com.softserveinc.mailsender.consumers;

import com.softserveinc.cross_api_objects.avro.AvroInvoiceAction;
import com.softserveinc.cross_api_objects.utils.correlation_id.CorrelationConstraints;
import com.softserveinc.cross_api_objects.utils.correlation_id.CorrelationManager;
import com.softserveinc.mailsender.MainConfig;
import com.softserveinc.mailsender.dao.UserDao;
import com.softserveinc.mailsender.services.MailSenderService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.URLDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.net.MalformedURLException;
import java.net.URL;

@Service
public class InvoiceActionConsumer {
    private static final Logger log= LoggerFactory.getLogger(InvoiceActionConsumer.class);

    private MailSenderService mailSenderService;
    private UserDao userDao;
    private MainConfig mainConfig;
    private Session session;

    @Autowired
    public InvoiceActionConsumer(MailSenderService mailSenderService, UserDao userDao, MainConfig mainConfig, Session session) {
        this.mailSenderService = mailSenderService;
        this.userDao = userDao;
        this.mainConfig = mainConfig;
        this.session = session;
    }

    @KafkaListener(topics = "#{@invoiceActionTopic}",containerFactory = "kafkaInvoiveActionListener")
    public void consume(ConsumerRecord<String, AvroInvoiceAction> record,
                        @Header(CorrelationConstraints.CORRELATION_ID_HEADER_NAME) String correlationId){
        CorrelationManager.setCorrelationId(correlationId);

        log.info("Consuming InvoiceAction. UserId: "+record.value().getUserId()+". Invoice link: "+record.value().getInvoice().getAttachmentUrl());

        if(userDao.findById(record.value().getUserId()).getEmailVerified()) {
            mailSenderService.sendMail(prepareMessage(record));
        }
        else{
            log.info("User's email is not verified. Don't send email.");
        }

        CorrelationManager.removeCorrelationId();
    }

    private MimeMessage prepareMessage(ConsumerRecord<String, AvroInvoiceAction> record){
        MimeMessage message=new MimeMessage(session);
        try {
            //create whole body
            Multipart multipart=new MimeMultipart();
            //create body text
            MimeBodyPart textBodyPart=new MimeBodyPart();
            textBodyPart.setText("Your invoice is in the attachment");

            //create attachment
            URL fileUrl=new URL(record.value().getInvoice().getAttachmentUrl().toString());
            DataSource dataSource=new URLDataSource(fileUrl);
            BodyPart filePart=new MimeBodyPart();
            filePart.setDataHandler(new DataHandler(dataSource));
            filePart.setFileName(record.value().getInvoice().getAttachmentName().toString());

            multipart.addBodyPart(filePart);
            multipart.addBodyPart(textBodyPart);

            //create message
            message.setFrom(new InternetAddress(mainConfig.getMailConfig().getFromAddress()));
            message.setRecipients(
                    Message.RecipientType.TO,
                    userDao.findById(record.value().getUserId()).getEmail()
            );
            message.setSubject("Bookstore invoice");
            message.setContent(multipart);

        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return message;
    }
}
