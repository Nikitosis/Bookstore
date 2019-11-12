package com.softserveinc.logger.consumers;

import com.softserveinc.cross_api_objects.api.UserBookPaymentLog;
import com.softserveinc.cross_api_objects.avro.AvroUserBookPaymentAction;
import com.softserveinc.logger.dao.UserBookPaymentLogDao;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
public class UserBookPaymentActionConsumer {
    private UserBookPaymentLogDao userBookPaymentLogDao;

    @Autowired
    public UserBookPaymentActionConsumer(UserBookPaymentLogDao userBookPaymentLogDao) {
        this.userBookPaymentLogDao = userBookPaymentLogDao;
    }

    @KafkaListener(topics = "#{@userBookPaymentActionTopic}",containerFactory = "userBookPaymentActionListener")
    public void consume(ConsumerRecord<String, AvroUserBookPaymentAction> record){
        UserBookPaymentLog userBookPaymentLog=new UserBookPaymentLog();
        userBookPaymentLog.setUserId(record.value().getUserId());
        userBookPaymentLog.setBookId(record.value().getBookId());
        userBookPaymentLog.setDate(LocalDateTime.parse(record.value().getDate()));
        userBookPaymentLog.setPayment(new BigDecimal(record.value().getPayment().toString()));

        userBookPaymentLogDao.save(userBookPaymentLog);
    }
}
