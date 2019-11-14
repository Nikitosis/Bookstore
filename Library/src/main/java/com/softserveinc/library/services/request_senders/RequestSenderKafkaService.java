package com.softserveinc.library.services.request_senders;

import com.softserveinc.cross_api_objects.api.Action;
import com.softserveinc.cross_api_objects.api.UserBookLog;
import com.softserveinc.cross_api_objects.avro.*;
import com.softserveinc.cross_api_objects.models.Mail;
import com.softserveinc.cross_api_objects.utils.correlation_id.CorrelationConstraints;
import com.softserveinc.cross_api_objects.utils.correlation_id.CorrelationManager;
import com.softserveinc.library.MainConfig;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
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
                .setDate(date.toString())
                .setStatus(AvroUserBookActionStatus.valueOf(action.toString()))
                .build();

        ProducerRecord<String,AvroUserBookAction> record=new ProducerRecord<String,AvroUserBookAction>(
                mainConfig.getKafkaUserBookActionTopic(),
                userId.toString(),
                avroUserBookAction
        );

        record.headers().add(CorrelationConstraints.CORRELATION_ID_HEADER_NAME, CorrelationManager.getCorrelationId().getBytes());

        kafkaUserBookActionProducer.send(record);
    }

    public void sendUserChangeEmailAction(Long userId, String newEmail, String verificationUrl){
        AvroUserChangedEmailAction avroUserChangedEmailAction=AvroUserChangedEmailAction.newBuilder()
                .setUserId(userId)
                .setNewEmail(newEmail)
                .setVerificationUrl(verificationUrl)
                .build();

        ProducerRecord<String,AvroUserChangedEmailAction> record=new ProducerRecord<String,AvroUserChangedEmailAction>(
                mainConfig.getKafkaUserChangedEmailActionTopic(),
                userId.toString(),
                avroUserChangedEmailAction
        );

        record.headers().add(CorrelationConstraints.CORRELATION_ID_HEADER_NAME, CorrelationManager.getCorrelationId().getBytes());

        kafkaUserChangedEmailAction.send(record);
    }
}
