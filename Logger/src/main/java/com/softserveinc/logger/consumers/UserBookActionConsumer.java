package com.softserveinc.logger.consumers;

import com.softserveinc.cross_api_objects.api.Action;
import com.softserveinc.cross_api_objects.api.UserBookLog;
import com.softserveinc.cross_api_objects.avro.AvroUserBookAction;
import com.softserveinc.cross_api_objects.utils.correlation_id.CorrelationConstraints;
import com.softserveinc.cross_api_objects.utils.correlation_id.CorrelationManager;
import com.softserveinc.logger.dao.UserBookLogDao;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.log4j.MDC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
public class UserBookActionConsumer {
    private static final Logger log=LoggerFactory.getLogger(UserBookActionConsumer.class);

    private UserBookLogDao userBookLogDao;

    @Autowired
    public UserBookActionConsumer(UserBookLogDao userBookLogDao) {
        this.userBookLogDao = userBookLogDao;
    }

    @KafkaListener(topics = "#{@userBookActionTopic}",containerFactory = "userBookActionListener")
    public void consume(ConsumerRecord<String, AvroUserBookAction> record,
                        @Header(CorrelationConstraints.CORRELATION_ID_HEADER_NAME) String correlationId){
        CorrelationManager.setCorrelationId(correlationId);
        log.info("Consuming UserBookAction. User id: "+record.value().getUserId()+". Book id: "+record.value().getBookId()+
                ". Status: "+record.value().getStatus().toString());

        UserBookLog userBookLog=new UserBookLog();
        userBookLog.setUserId(record.value().getUserId());
        userBookLog.setBookId(record.value().getBookId());
        userBookLog.setAction(Action.valueOf(record.value().getStatus().toString()));
        userBookLog.setDate(LocalDateTime.parse(record.value().getDate()));

        userBookLogDao.save(userBookLog);

        CorrelationManager.removeCorrelationId();
    }
}
