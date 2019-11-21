package com.softserveinc.library.services.request_senders;

import com.softserveinc.cross_api_objects.api.Action;
import com.softserveinc.cross_api_objects.avro.*;
import com.softserveinc.cross_api_objects.utils.correlation_id.CorrelationConstraints;
import com.softserveinc.cross_api_objects.utils.correlation_id.CorrelationManager;
import com.softserveinc.library.MainConfig;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service("requestSenderKafkaService")
public class RequestSenderKafkaService{
    private static final Logger log=LoggerFactory.getLogger(RequestSenderKafkaService.class);

    private MainConfig mainConfig;
    private Producer<String, AvroUserBookAction> kafkaUserBookActionProducer;
    private Producer<String,AvroUserChangedEmailAction> kafkaUserChangedEmailActionProducer;
    private Producer<String,AvroBookAction> kafkaBookActionProducer;

    @Autowired
    public RequestSenderKafkaService(MainConfig mainConfig, Producer<String, AvroUserBookAction> kafkaUserBookActionProducer, Producer<String, AvroUserChangedEmailAction> kafkaUserChangedEmailActionProducer, Producer<String, AvroBookAction> kafkaBookActionProducer) {
        this.mainConfig = mainConfig;
        this.kafkaUserBookActionProducer = kafkaUserBookActionProducer;
        this.kafkaUserChangedEmailActionProducer = kafkaUserChangedEmailActionProducer;
        this.kafkaBookActionProducer = kafkaBookActionProducer;
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

        log.info("Sending UserBookAction. User id: "+record.value().getUserId()+". Book id: "+record.value().getBookId()+
                ". Status: "+record.value().getStatus().toString());
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

        log.info("Sending UserChangedEmailAction. User id: "+record.value().getUserId()+". New email: "+record.value().getNewEmail());

        kafkaUserChangedEmailActionProducer.send(record);
    }

    public void sendBookAction(Long bookId,AvroBookActionStatus status){
        AvroBookAction bookAction=AvroBookAction.newBuilder()
                .setBookId(bookId)
                .setStatus(status)
                .build();

        ProducerRecord<String,AvroBookAction> record=new ProducerRecord<String,AvroBookAction>(
                mainConfig.getKafkaBookActionTopic(),
                bookId.toString(),
                bookAction
        );

        record.headers().add(CorrelationConstraints.CORRELATION_ID_HEADER_NAME, CorrelationManager.getCorrelationId().getBytes());

        log.info("Sending AvroBookAction. Book id: "+record.value().getBookId()+". Status: "+record.value().getStatus().toString());

        kafkaBookActionProducer.send(record);
    }
}
