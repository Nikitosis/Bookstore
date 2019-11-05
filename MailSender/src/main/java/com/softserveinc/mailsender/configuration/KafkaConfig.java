package com.softserveinc.mailsender.configuration;

import com.softserveinc.mailsender.MainConfig;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

import java.util.HashMap;
import java.util.Map;

@EnableKafka
@Configuration
public class KafkaConfig {
    private MainConfig mainConfig;

    @Autowired
    public KafkaConfig(MainConfig mainConfig) {
        this.mainConfig = mainConfig;
    }

    @Bean
    public ConsumerFactory<String,Mail> consumerFactory(){
        Map<String,Object> props=new HashMap<String, Object>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,mainConfig.getKafkaConfig().getBrokerUrl());
        props.put(ConsumerConfig.GROUP_ID_CONFIG,"MailConsumers");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class);
        props.put("schema.registry.url",mainConfig.getKafkaConfig().getSchemaRegistryUrl());
        props.put(KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG, "true");
        return new DefaultKafkaConsumerFactory<String, Mail>(props);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String,Mail> kafkaListenerContainerFactory(){
        ConcurrentKafkaListenerContainerFactory<String,Mail> factory=new ConcurrentKafkaListenerContainerFactory<String,Mail>();
        factory.setConsumerFactory(consumerFactory());
        factory.getContainerProperties().setPollTimeout(1000);
        return factory;
    }

}
