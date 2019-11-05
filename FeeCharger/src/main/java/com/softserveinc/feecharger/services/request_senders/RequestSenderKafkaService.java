package com.softserveinc.feecharger.services.request_senders;

import com.softserveinc.cross_api_objects.avro.AvroConverter;
import com.softserveinc.cross_api_objects.avro.AvroMail;
import com.softserveinc.cross_api_objects.models.Mail;
import com.softserveinc.feecharger.MainConfig;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.stereotype.Service;

@Service("requestSenderKafkaService")
public class RequestSenderKafkaService implements MailSenderService{
    private MainConfig mainConfig;
    private Producer<String,AvroMail> mailProducer;

    public RequestSenderKafkaService(MainConfig mainConfig, Producer<String, AvroMail> mailProducer) {
        this.mainConfig = mainConfig;
        this.mailProducer = mailProducer;
    }

    @Override
    public void sendEmail(Mail mail) {
       mailProducer.send(new ProducerRecord<String,AvroMail>(
               mainConfig.getKafkaMailTopic(),
               AvroConverter.buildAvroMail(mail)
       ));
    }

}
