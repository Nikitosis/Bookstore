package com.softserveinc.mailsender.consumers;

import com.softserveinc.mailsender.services.MailSenderService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class MailConsumer {
    private MailSenderService mailSenderService;

    @Autowired
    public MailConsumer(MailSenderService mailSenderService) {
        this.mailSenderService = mailSenderService;
    }

    @KafkaListener(topics = "MailTopic")
    public void consumeMail(ConsumerRecord<String,Mail> record){
        mailSenderService.sendMail(buildMail(record.value()));
    }

    private com.softserveinc.cross_api_objects.models.Mail buildMail(Mail avroMail){
        com.softserveinc.cross_api_objects.models.Mail mail=new com.softserveinc.cross_api_objects.models.Mail();
        mail.setBody(avroMail.getBody().toString());
        mail.setReceiverEmail(avroMail.getReceiverEmaill().toString());
        mail.setSubject(avroMail.getSubject().toString());
        return mail;
    }
}
