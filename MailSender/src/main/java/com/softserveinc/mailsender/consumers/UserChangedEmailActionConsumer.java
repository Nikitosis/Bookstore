package com.softserveinc.mailsender.consumers;

import com.softserveinc.cross_api_objects.avro.AvroUserChangedEmailAction;
import com.softserveinc.cross_api_objects.utils.correlation_id.CorrelationConstraints;
import com.softserveinc.cross_api_objects.utils.correlation_id.CorrelationManager;
import com.softserveinc.mailsender.MainConfig;
import com.softserveinc.mailsender.services.MailSenderService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

@Service
public class UserChangedEmailActionConsumer {
    private static final Logger log= LoggerFactory.getLogger(InvoiceActionConsumer.class);

    private MailSenderService mailSenderService;

    private MainConfig mainConfig;

    private Session session;

    @Autowired
    public UserChangedEmailActionConsumer(MailSenderService mailSenderService, MainConfig mainConfig, Session session) {
        this.mailSenderService = mailSenderService;
        this.mainConfig = mainConfig;
        this.session = session;
    }

    @KafkaListener(topics = "#{@userChangedEmailActionTopic}",containerFactory = "kafkaUserChangedEmailActionListener")
    public void consume(ConsumerRecord<String, AvroUserChangedEmailAction> record,
                        @Header(CorrelationConstraints.CORRELATION_ID_HEADER_NAME) String correlationId){
        CorrelationManager.setCorrelationId(correlationId);

        log.info("Consuming UserChangedEmailAction. User id: "+record.value().getUserId()+". New email: "+record.value().getNewEmail());

        mailSenderService.sendMail(prepareMessage(record));

        CorrelationManager.removeCorrelationId();
    }

    private MimeMessage prepareMessage(ConsumerRecord<String, AvroUserChangedEmailAction> record){
        MimeMessage message=new MimeMessage(session);
        try {
            //create whole body
            Multipart multipart=new MimeMultipart();
            //create body text
            MimeBodyPart textBodyPart=new MimeBodyPart();
            textBodyPart.setText("Please, verify your email here: "+record.value().getVerificationUrl());

            multipart.addBodyPart(textBodyPart);

            //create message
            message.setFrom(new InternetAddress(mainConfig.getMailConfig().getFromAddress()));
            message.setRecipients(
                Message.RecipientType.TO,
                record.value().getNewEmail().toString()
            );
            message.setSubject("Please, verify your email");
            message.setContent(multipart);

        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return message;
    }
}
