package com.softserveinc.mailsender.consumers;

import com.softserveinc.cross_api_objects.avro.AvroUserChangedEmailAction;
import com.softserveinc.cross_api_objects.models.Mail;
import com.softserveinc.cross_api_objects.utils.correlation_id.CorrelationConstraints;
import com.softserveinc.cross_api_objects.utils.correlation_id.CorrelationManager;
import com.softserveinc.mailsender.services.MailSenderService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

@Service
public class UserChangedEmailActionConsumer {
    private static final Logger log= LoggerFactory.getLogger(InvoiceActionConsumer.class);

    private MailSenderService mailSenderService;

    @Autowired
    public UserChangedEmailActionConsumer(MailSenderService mailSenderService) {
        this.mailSenderService = mailSenderService;
    }

    @KafkaListener(topics = "#{@userChangedEmailActionTopic}",containerFactory = "kafkaUserChangedEmailActionListener")
    public void consume(ConsumerRecord<String, AvroUserChangedEmailAction> record,
                        @Header(CorrelationConstraints.CORRELATION_ID_HEADER_NAME) String correlationId){
        CorrelationManager.setCorrelationId(correlationId);

        log.info("Consuming UserChangedEmailAction. User id: "+record.value().getUserId()+". New email: "+record.value().getNewEmail());

        Mail mail =new Mail();
        mail.setReceiverEmail(record.value().getNewEmail().toString());
        mail.setSubject("Please, verify your email");
        mail.setBody("Please, verify your email here: "+record.value().getVerificationUrl());

        mailSenderService.sendMail(mail);

        CorrelationManager.removeCorrelationId();
    }
}
