package com.softserveinc.library.services.request_senders;

import com.softserveinc.cross_api_objects.api.UserBookLog;
import com.softserveinc.cross_api_objects.avro.AvroConverter;
import com.softserveinc.cross_api_objects.avro.AvroMail;
import com.softserveinc.cross_api_objects.avro.AvroUserBookLog;
import com.softserveinc.cross_api_objects.models.Mail;
import com.softserveinc.library.MainConfig;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("requestSenderKafkaService")
public class RequestSenderKafkaService implements MailSenderService,LogSenderService {
    private MainConfig mainConfig;
    private Producer<String, AvroMail> kafkaMailProducer;
    private Producer<String,AvroUserBookLog> kafkaUserBookLogProducer;

    @Autowired
    public RequestSenderKafkaService(MainConfig mainConfig, Producer<String, AvroMail> kafkaMailProducer, Producer<String, AvroUserBookLog> kafkaUserBookLogProducer) {
        this.mainConfig = mainConfig;
        this.kafkaMailProducer = kafkaMailProducer;
        this.kafkaUserBookLogProducer = kafkaUserBookLogProducer;
    }

    @Override
    public void sendUserBookLog(UserBookLog userBookLog) {
        kafkaUserBookLogProducer.send(new ProducerRecord<String,AvroUserBookLog>(
                mainConfig.getKafkaUserBookLogTopic(),
                AvroConverter.buildAvroUserBookLog(userBookLog)
        ));
    }

    @Override
    public void sendEmail(Mail mail) {

        kafkaMailProducer.send(new ProducerRecord<String, AvroMail>(
                mainConfig.getKafkaMailTopic(),
                AvroConverter.buildAvroMail(mail)
        ));
    }

}
