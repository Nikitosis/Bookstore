package com.softserveinc.logger.consumers;

import com.softserveinc.cross_api_objects.api.UserBookPaymentLog;
import com.softserveinc.cross_api_objects.avro.AvroUserBookPaymentAction;
import com.softserveinc.cross_api_objects.utils.correlation_id.CorrelationConstraints;
import com.softserveinc.cross_api_objects.utils.correlation_id.CorrelationManager;
import com.softserveinc.logger.dao.UserBookPaymentLogDao;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
public class UserBookPaymentActionConsumer {
    private static final Logger log= LoggerFactory.getLogger(UserBookActionConsumer.class);

    private UserBookPaymentLogDao userBookPaymentLogDao;

    @Autowired
    public UserBookPaymentActionConsumer(UserBookPaymentLogDao userBookPaymentLogDao) {
        this.userBookPaymentLogDao = userBookPaymentLogDao;
    }

    @KafkaListener(topics = "#{@userBookPaymentActionTopic}",containerFactory = "userBookPaymentActionListener")
    public void consume(ConsumerRecord<String, AvroUserBookPaymentAction> record,
                        @Header(CorrelationConstraints.CORRELATION_ID_HEADER_NAME) String correlationId){
        CorrelationManager.setCorrelationId(correlationId);
        log.info("Handling userBookPaymentAction");

        UserBookPaymentLog userBookPaymentLog=new UserBookPaymentLog();
        userBookPaymentLog.setUserId(record.value().getUserId());
        userBookPaymentLog.setBookId(record.value().getBookId());
        userBookPaymentLog.setDate(LocalDateTime.parse(record.value().getDate()));
        userBookPaymentLog.setPayment(new BigDecimal(record.value().getPayment().toString()));

        userBookPaymentLogDao.save(userBookPaymentLog);

        CorrelationManager.removeCorrelationId();
    }
}
