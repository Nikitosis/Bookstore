package com.softserveinc.logger.consumers;

import com.softserveinc.cross_api_objects.avro.AvroConverter;
import com.softserveinc.cross_api_objects.avro.AvroMail;
import com.softserveinc.cross_api_objects.avro.AvroUserBookLog;
import com.softserveinc.logger.MainConfig;
import com.softserveinc.logger.dao.UserBookLogDao;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

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
