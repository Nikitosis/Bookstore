package com.softserveinc.feecharger.services.request_senders;

import com.softserveinc.cross_api_objects.avro.*;
import com.softserveinc.cross_api_objects.utils.correlation_id.CorrelationConstraints;
import com.softserveinc.cross_api_objects.utils.correlation_id.CorrelationManager;
import com.softserveinc.feecharger.MainConfig;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service("requestSenderKafkaService")
public class RequestSenderKafkaService{
    private static final Logger log= LoggerFactory.getLogger(RequestSenderKafkaService.class);

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
                .setStatus(actionStatus)
                .build();
        ProducerRecord<String,AvroUserBookExtendAction> record=new ProducerRecord<String,AvroUserBookExtendAction>(
                mainConfig.getKafkaUserBookExtendActionTopic(),
                userId.toString(),
                avroUserBookExtendAction
        );
        record.headers().add(CorrelationConstraints.CORRELATION_ID_HEADER_NAME, CorrelationManager.getCorrelationId().getBytes());

        log.info("Sending UserBookExtendAction. User id: "+record.value().getUserId()+". Book id: "+
                record.value().getBookId()+". Status: "+record.value().getStatus().toString());

        userBookExtendActionProducer.send(record);
    }

    public void sendUserBookPaymentAction(Long userId, Long bookId, BigDecimal payment, LocalDateTime dateTime){
       AvroUserBookPaymentAction avroUserBookPaymentAction=AvroUserBookPaymentAction.newBuilder()
                .setUserId(userId)
                .setBookId(bookId)
                .setPayment(payment.toString())
                .setDate(dateTime.toString())
                .build();

        ProducerRecord<String,AvroUserBookPaymentAction> record=new ProducerRecord<String,AvroUserBookPaymentAction>(
                mainConfig.getKafkaUserBookPaymentActionTopic(),
                userId.toString(),
                avroUserBookPaymentAction
        );

        record.headers().add(CorrelationConstraints.CORRELATION_ID_HEADER_NAME, CorrelationManager.getCorrelationId().getBytes());

        log.info("Sending UserBookExtendAction. User id: "+record.value().getUserId()+". Book id: "+
                record.value().getBookId()+". Status: "+record.value().getPayment().toString());

        userBookPaymentActionProducer.send(record);
    }
}
