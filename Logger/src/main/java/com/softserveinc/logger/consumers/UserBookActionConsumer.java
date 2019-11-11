package com.softserveinc.logger.consumers;

import com.softserveinc.cross_api_objects.api.Action;
import com.softserveinc.cross_api_objects.api.UserBookLog;
import com.softserveinc.cross_api_objects.avro.AvroConverter;
import com.softserveinc.cross_api_objects.avro.AvroUserBookAction;
import com.softserveinc.cross_api_objects.avro.AvroUserBookLog;
import com.softserveinc.logger.dao.UserBookLogDao;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;

@Service
public class UserBookActionConsumer {
    private UserBookLogDao userBookLogDao;

    @Autowired
    public UserBookActionConsumer(UserBookLogDao userBookLogDao) {
        this.userBookLogDao = userBookLogDao;
    }

    @KafkaListener(topics = "#{@userBookActionTopic}",containerFactory = "userBookActionListener")
    public void consumeUserBookLog(ConsumerRecord<String, AvroUserBookAction> record){
        UserBookLog userBookLog=new UserBookLog();
        userBookLog.setUserId(record.value().getUserId());
        userBookLog.setBookId(record.value().getBookId());
        userBookLog.setAction(Action.valueOf(record.value().getAction().toString()));
        userBookLog.setDate(Instant.ofEpochMilli(record.value().getDate()).atZone(ZoneId.systemDefault()).toLocalDateTime());

        userBookLogDao.save(userBookLog);
    }
}
