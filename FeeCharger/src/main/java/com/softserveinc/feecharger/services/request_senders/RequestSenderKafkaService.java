package com.softserveinc.feecharger.services.request_senders;

import com.softserveinc.cross_api_objects.avro.AvroConverter;
import com.softserveinc.cross_api_objects.avro.Mail;
import com.softserveinc.cross_api_objects.models.Book;
import com.softserveinc.cross_api_objects.models.User;
import com.softserveinc.feecharger.MainConfig;
import com.softserveinc.feecharger.dao.BookDao;
import com.softserveinc.feecharger.dao.UserDao;
import com.softserveinc.feecharger.models.UserBook;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service("requestSenderKafkaService")
public class RequestSenderKafkaService implements MailSenderService{
    private MainConfig mainConfig;
    private Producer<String,Mail> mailProducer;

    public RequestSenderKafkaService(MainConfig mainConfig, Producer<String, Mail> mailProducer) {
        this.mainConfig = mainConfig;
        this.mailProducer = mailProducer;
    }

    @Override
    public void sendEmail(com.softserveinc.cross_api_objects.models.Mail mail) {
       mailProducer.send(new ProducerRecord<String,Mail>(
               mainConfig.getKafkaMailTopic(),
               AvroConverter.buildAvroMail(mail)
       ));
    }

}
