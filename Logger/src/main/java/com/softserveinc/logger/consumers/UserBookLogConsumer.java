package com.softserveinc.logger.consumers;

import com.softserveinc.cross_api_objects.avro.AvroConverter;
import com.softserveinc.cross_api_objects.avro.AvroUserBookLog;
import com.softserveinc.logger.dao.UserBookLogDao;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class UserBookLogConsumer {
    private UserBookLogDao userBookLogDao;

    @Autowired
    public UserBookLogConsumer(UserBookLogDao userBookLogDao) {
        this.userBookLogDao = userBookLogDao;
    }

    @KafkaListener(topics = "#{@userBookLogTopic}",containerFactory = "userBookLogListener")
    public void consumeUserBookLog(ConsumerRecord<String,AvroUserBookLog> record){
        userBookLogDao.save(AvroConverter.buildUserBookLog(record.value()));
    }
}
