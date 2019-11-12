package com.softserveinc.feecharger.services.request_senders;

import com.softserveinc.cross_api_objects.api.UserBookPaymentLog;
import com.softserveinc.cross_api_objects.avro.*;
import com.softserveinc.cross_api_objects.models.Mail;
import com.softserveinc.feecharger.MainConfig;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service("requestSenderKafkaService")
public class RequestSenderKafkaService{
    private MainConfig mainConfig;

    private Producer<String, AvroUserBookExtendAction> userBookExtendActionProducer;
    private Producer<String,AvroUserBookPaymentAction> userBookPaymentActionProducer;

    @Autowired
    public RequestSenderKafkaService(MainConfig mainConfig, Producer<String, AvroUserBookExtendAction> userBookExtendActionProducer, Producer<String, AvroUserBookPaymentAction> userBookPaymentActionProducer) {
        this.mainConfig = mainConfig;
        this.userBookExtendActionProducer = userBookExtendActionProducer;
        this.userBookPaymentActionProducer = userBookPaymentActionProducer;
    }

    public void sendUserBookExtendAction(Long userId, Long bookId, AvroUserBookExtendActionStatus actionStatus){
       AvroUserBookExtendAction avroUserBookExtendAction=AvroUserBookExtendAction.newBuilder()
                .setUserId(userId)
                .setBookId(bookId)
                .setAction(actionStatus)
                .build();

        userBookExtendActionProducer.send(new ProducerRecord<String,AvroUserBookExtendAction>(
                mainConfig.getKafkaUserBookExtendActionTopic(),
                avroUserBookExtendAction
        ));
    }

    public void sendUserBookPaymentAction(Long userId, Long bookId, BigDecimal payment, LocalDateTime dateTime){
       AvroUserBookPaymentAction avroUserBookPaymentAction=AvroUserBookPaymentAction.newBuilder()
                .setUserId(userId)
                .setBookId(bookId)
                .setPayment(payment.toString())
                .setDate(dateTime.toString())
                .build();

        userBookPaymentActionProducer.send(new ProducerRecord<String,AvroUserBookPaymentAction>(
                mainConfig.getKafkaUserBookPaymentActionTopic(),
                avroUserBookPaymentAction
        ));
    }
}
