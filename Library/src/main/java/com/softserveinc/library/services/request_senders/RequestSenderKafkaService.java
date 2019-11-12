package com.softserveinc.library.services.request_senders;

import com.softserveinc.cross_api_objects.api.Action;
import com.softserveinc.cross_api_objects.api.UserBookLog;
import com.softserveinc.cross_api_objects.avro.*;
import com.softserveinc.cross_api_objects.models.Mail;
import com.softserveinc.library.MainConfig;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service("requestSenderKafkaService")
public class RequestSenderKafkaService{
    private MainConfig mainConfig;
    private Producer<String, AvroUserBookAction> kafkaUserBookActionProducer;
    private Producer<String,AvroUserChangedEmailAction> kafkaUserChangedEmailAction;

    @Autowired
    public RequestSenderKafkaService(MainConfig mainConfig, Producer<String, AvroUserBookAction> kafkaUserBookActionProducer, Producer<String, AvroUserChangedEmailAction> kafkaUserChangedEmailAction) {
        this.mainConfig = mainConfig;
        this.kafkaUserBookActionProducer = kafkaUserBookActionProducer;
        this.kafkaUserChangedEmailAction = kafkaUserChangedEmailAction;
    }

    public void sendUserBookAction(Long userId, Long bookId, LocalDateTime date, Action action){
        AvroUserBookAction avroUserBookAction=AvroUserBookAction.newBuilder()
                .setUserId(userId)
                .setBookId(bookId)
                .setDate(date.toInstant(ZoneOffset.UTC).toEpochMilli())
                .setAction(AvroUserBookActionStatus.valueOf(action.toString()))
                .build();

        kafkaUserBookActionProducer.send(new ProducerRecord<String,AvroUserBookAction>(
                mainConfig.getKafkaUserBookActionTopic(),
                avroUserBookAction
        ));
    }

    public void sendUserChangeEmailAction(Long userId, String newEmail, String verificationUrl){
        AvroUserChangedEmailAction avroUserChangedEmailAction=AvroUserChangedEmailAction.newBuilder()
                .setUserId(userId)
                .setNewEmail(newEmail)
                .setVerificationUrl(verificationUrl)
                .build();

        kafkaUserChangedEmailAction.send(new ProducerRecord<String,AvroUserChangedEmailAction>(
                mainConfig.getKafkaUserChangedEmailActionTopic(),
                avroUserChangedEmailAction
        ));
    }
}
