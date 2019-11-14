package com.softserveinc.mailsender.consumers;

import com.softserveinc.cross_api_objects.avro.AvroUserChangedEmailAction;
import com.softserveinc.cross_api_objects.models.Mail;
import com.softserveinc.mailsender.services.MailSenderService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class UserChangedEmailActionConsumer {
    private MailSenderService mailSenderService;

    @Autowired
    public UserChangedEmailActionConsumer(MailSenderService mailSenderService) {
        this.mailSenderService = mailSenderService;
    }

    @KafkaListener(topics = "#{@userChangedEmailActionTopic}",containerFactory = "kafkaUserChangedEmailActionListener")
    public void consume(ConsumerRecord<String, AvroUserChangedEmailAction> record){

        Mail mail =new Mail();
        mail.setReceiverEmail(record.value().getNewEmail().toString());
        mail.setSubject("Please, verify your email");
        mail.setBody("Please, verify your email here: "+record.value().getVerificationUrl());

        mailSenderService.sendMail(mail);
    }
}