package com.softserveinc.invoicer.configurations;

import com.softserveinc.cross_api_objects.avro.AvroMail;
import com.softserveinc.cross_api_objects.avro.AvroUserBookPaymentLog;
import com.softserveinc.invoicer.MainConfig;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaAppConfig {

    private MainConfig mainConfig;

    @Autowired
    public KafkaAppConfig(MainConfig mainConfig) {
        this.mainConfig = mainConfig;
    }

    @Bean
    public Map<String,Object> producerConfigs(){
        Map<String,Object> props=new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,mainConfig.getKafkaConfig().getBrokerUrl());
        props.put(ProducerConfig.ACKS_CONFIG,"all");
        props.put(ProducerConfig.RETRIES_CONFIG,0);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,KafkaAvroSerializer.class);
        props.put("schema.registry.url",mainConfig.getKafkaConfig().getSchemaRegistryUrl());
        return props;
    }

    @Bean
    public Producer<String,AvroMail> kafkaMailProducer(){
        return new KafkaProducer<String, AvroMail>(producerConfigs());
    }
}
