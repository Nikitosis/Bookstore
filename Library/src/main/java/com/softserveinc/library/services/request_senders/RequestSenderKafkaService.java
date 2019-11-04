package com.softserveinc.library.services.request_senders;

import com.softserveinc.cross_api_objects.api.UserBookLog;
import com.softserveinc.cross_api_objects.models.Mail;
import com.softserveinc.library.MainConfig;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("requestSenderKafkaService")
public class RequestSenderKafkaService implements RequestSenderService {
    private MainConfig mainConfig;
    private Producer<String, com.softserveinc.cross_api_objects.avro.Mail> kafkaProducer;

    @Autowired
    public RequestSenderKafkaService(MainConfig mainConfig, Producer<String, com.softserveinc.cross_api_objects.avro.Mail> kafkaProducer) {
        this.mainConfig = mainConfig;
        this.kafkaProducer = kafkaProducer;
    }

    @Override
    public void postUserBookLog(UserBookLog userBookLog) {

    }

    @Override
    public void postChargeBookFee(Long userId, Long bookId) {

    }

    @Override
    public void sendEmailVerification(Mail mail) {
        com.softserveinc.cross_api_objects.avro.Mail avroMail=buildAvroMail(mail);

        kafkaProducer.send(new ProducerRecord<String, com.softserveinc.cross_api_objects.avro.Mail>(
                mainConfig.getKafkaMailConfig().getTopic(),
                avroMail
        ));
    }

    private com.softserveinc.cross_api_objects.avro.Mail buildAvroMail(Mail mail){
        com.softserveinc.cross_api_objects.avro.Mail avroMail=new com.softserveinc.cross_api_objects.avro.Mail();
        avroMail.setBody(mail.getBody());
        avroMail.setReceiverEmaill(mail.getReceiverEmail());
        avroMail.setSubject(mail.getSubject());
        return avroMail;
    }


}
