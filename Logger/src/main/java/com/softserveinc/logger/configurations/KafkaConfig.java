package com.softserveinc.logger.configurations;

import com.softserveinc.cross_api_objects.avro.AvroUserBookAction;
import com.softserveinc.cross_api_objects.avro.AvroUserBookPaymentAction;
import com.softserveinc.logger.MainConfig;
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
    public Map<String,Object> consumerConfig(){
        Map<String,Object> props=new HashMap<String, Object>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,mainConfig.getKafkaConfig().getBrokerUrl());
        props.put(ConsumerConfig.GROUP_ID_CONFIG,"LoggerConsumers");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
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
    public ConsumerFactory<String, AvroUserBookPaymentAction> userBookPaymentActionConsumerFactory(){
        return new DefaultKafkaConsumerFactory<String, AvroUserBookPaymentAction>(consumerConfig());
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String,AvroUserBookPaymentAction> userBookPaymentActionListener(){
        ConcurrentKafkaListenerContainerFactory<String,AvroUserBookPaymentAction> factory=new ConcurrentKafkaListenerContainerFactory<String,AvroUserBookPaymentAction>();
        factory.setConsumerFactory(userBookPaymentActionConsumerFactory());
        factory.getContainerProperties().setPollTimeout(1000);
        return factory;
    }

    @Bean
    public String userBookActionTopic(){
        return mainConfig.getKafkaUserBookActionTopic();
    }

    @Bean
    public String userBookPaymentActionTopic(){
        return mainConfig.getKafkaUserBookPaymentActionTopic();
    }
}
