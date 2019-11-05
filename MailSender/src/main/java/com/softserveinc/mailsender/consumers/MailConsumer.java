package com.softserveinc.mailsender.consumers;

import com.softserveinc.cross_api_objects.avro.AvroConverter;
import com.softserveinc.cross_api_objects.avro.AvroMail;
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

    @KafkaListener(topics = "MailTopic",containerFactory = "consumerFactory")
    public void consumeMail(ConsumerRecord<String,AvroMail> record){
        mailSenderService.sendMail(AvroConverter.buildMail(record.value()));
    }

}
