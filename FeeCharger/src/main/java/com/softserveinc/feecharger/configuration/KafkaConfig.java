package com.softserveinc.feecharger.configuration;

import com.softserveinc.cross_api_objects.avro.*;
import com.softserveinc.feecharger.MainConfig;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaConfig {

    private MainConfig mainConfig;

    @Autowired
    public KafkaConfig(MainConfig mainConfig) {
        this.mainConfig = mainConfig;
    }

    @Bean
    public Map<String,Object> producerConfigs(){
        Map<String,Object> props=new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,mainConfig.getKafkaConfig().getBrokerUrl());
        props.put(ProducerConfig.ACKS_CONFIG,"all");
        props.put(ProducerConfig.RETRIES_CONFIG,0);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,KafkaAvroSerializer.class);
        props.put("schema.registry.url",mainConfig.getKafkaConfig().getSchemaRegistryUrl());
        return props;
    }

    @Bean
    public Map<String,Object> consumerConfig(){
        Map<String,Object> props=new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,mainConfig.getKafkaConfig().getBrokerUrl());
        props.put(ConsumerConfig.GROUP_ID_CONFIG,"FeeChargerConsumers");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class);
        props.put("schema.registry.url",mainConfig.getKafkaConfig().getSchemaRegistryUrl());
        props.put(KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG, "true");
        return props;
    }

    @Bean
    public ConsumerFactory<String, AvroUserBookAction> userBookActionConsumerFactory(){
        return new DefaultKafkaConsumerFactory<String, AvroUserBookAction>(consumerConfig());
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String,AvroUserBookAction> userBookActionListener(){
        ConcurrentKafkaListenerContainerFactory<String, AvroUserBookAction> factory=new ConcurrentKafkaListenerContainerFactory<String,AvroUserBookAction>();
        factory.setConsumerFactory(userBookActionConsumerFactory());
        factory.getContainerProperties().setPollTimeout(1000);
        return factory;
    }

    @Bean
    public Producer<String, AvroUserBookExtendAction> kafkaUserBookExtendActionProducer(){
        return new KafkaProducer<String, AvroUserBookExtendAction>(producerConfigs());
    }

    @Bean
    public Producer<String, AvroUserBookPaymentAction> kafkaUserBookPaymentActionProducer(){
        return new KafkaProducer<String,AvroUserBookPaymentAction>(producerConfigs());
    }

    @Bean
    public String userBookActionTopic(){
        return mainConfig.getKafkaUserBookActionTopic();
    }
}
