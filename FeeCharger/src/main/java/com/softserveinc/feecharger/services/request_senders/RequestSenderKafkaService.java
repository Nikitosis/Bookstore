package com.softserveinc.feecharger.services.request_senders;

import com.softserveinc.cross_api_objects.api.UserBookPaymentLog;
import com.softserveinc.cross_api_objects.avro.AvroConverter;
import com.softserveinc.cross_api_objects.avro.AvroMail;
import com.softserveinc.cross_api_objects.avro.AvroUserBookPaymentLog;
import com.softserveinc.cross_api_objects.models.Mail;
import com.softserveinc.feecharger.MainConfig;
import com.softserveinc.feecharger.models.UserBook;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service("requestSenderKafkaService")
public class RequestSenderKafkaService implements MailSenderService,LogSenderService {
    private MainConfig mainConfig;
    private Producer<String,AvroMail> mailProducer;
    private Producer<String,AvroUserBookPaymentLog> userBookPaymentLogProducer;

    @Autowired
    public RequestSenderKafkaService(MainConfig mainConfig, Producer<String, AvroMail> mailProducer, Producer<String, AvroUserBookPaymentLog> userBookPaymentLogProducer) {
        this.mainConfig = mainConfig;
        this.mailProducer = mailProducer;
        this.userBookPaymentLogProducer = userBookPaymentLogProducer;
    }

    @Override
    public void sendPaymentLog(UserBookPaymentLog userBookPaymentLog) {
        userBookPaymentLogProducer.send(new ProducerRecord<String,AvroUserBookPaymentLog>(
                mainConfig.getKafkaUserBookPaymentLogTopic(),
                AvroConverter.buildAvroUserBookPaymentLog(userBookPaymentLog)
        ));
    }

    @Override
    public void sendEmail(Mail mail) {
       mailProducer.send(new ProducerRecord<String,AvroMail>(
               mainConfig.getKafkaMailTopic(),
               AvroConverter.buildAvroMail(mail)
       ));
    }

}
