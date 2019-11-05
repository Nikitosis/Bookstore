package com.softserveinc.library.services.request_senders;

import com.softserveinc.cross_api_objects.api.UserBookLog;
import com.softserveinc.cross_api_objects.avro.AvroConverter;
import com.softserveinc.cross_api_objects.models.Mail;
import com.softserveinc.library.MainConfig;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("requestSenderKafkaService")
public class RequestSenderKafkaService implements MailSenderService,LogSenderService {
    private MainConfig mainConfig;
    private Producer<String, com.softserveinc.cross_api_objects.avro.Mail> kafkaProducer;

    @Autowired
    public RequestSenderKafkaService(MainConfig mainConfig, Producer<String, com.softserveinc.cross_api_objects.avro.Mail> kafkaProducer) {
        this.mainConfig = mainConfig;
        this.kafkaProducer = kafkaProducer;
    }

    @Override
    public void sendUserBookLog(UserBookLog userBookLog) {

    }

    @Override
    public void sendEmail(Mail mail) {

        kafkaProducer.send(new ProducerRecord<String, com.softserveinc.cross_api_objects.avro.Mail>(
                mainConfig.getKafkaMailTopic(),
                AvroConverter.buildAvroMail(mail)
        ));
    }

}
