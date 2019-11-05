package com.softserveinc.logger.consumers;

import com.softserveinc.cross_api_objects.avro.AvroConverter;
import com.softserveinc.cross_api_objects.avro.AvroUserBookPaymentLog;
import com.softserveinc.logger.dao.UserBookPaymentLogDao;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class UserBookPaymentLogConsumer {
    private UserBookPaymentLogDao userBookPaymentLogDao;

    @Autowired
    public UserBookPaymentLogConsumer(UserBookPaymentLogDao userBookPaymentLogDao) {
        this.userBookPaymentLogDao = userBookPaymentLogDao;
    }

    @KafkaListener(topics = "UserBookPaymentLogTopic",containerFactory = "userBookPaymentLogListener")
    public void consumeUserBookPaymentLogTopic(ConsumerRecord<String,AvroUserBookPaymentLog> record){
        userBookPaymentLogDao.save(AvroConverter.buildUserBookPaymentLog(record.value()));
    }
}
