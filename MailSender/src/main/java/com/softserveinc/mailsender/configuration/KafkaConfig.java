package com.softserveinc.mailsender.configuration;

import com.softserveinc.cross_api_objects.avro.*;
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
    public Map<String,Object> consumerConfig(){
        Map<String,Object> props=new HashMap<String, Object>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,mainConfig.getKafkaConfig().getBrokerUrl());
        props.put(ConsumerConfig.GROUP_ID_CONFIG,"MailConsumers");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class);
        props.put("schema.registry.url",mainConfig.getKafkaConfig().getSchemaRegistryUrl());
        props.put(KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG, "true");
        return props;
    }

    @Bean
    public ConsumerFactory<String, AvroUserChangedEmailAction> userChangedEmailActionConsumerFactory(){
        return new DefaultKafkaConsumerFactory<String,AvroUserChangedEmailAction>(consumerConfig());
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String,AvroUserChangedEmailAction> kafkaUserChangedEmailActionListener(){
        ConcurrentKafkaListenerContainerFactory<String,AvroUserChangedEmailAction> factory=new ConcurrentKafkaListenerContainerFactory<String,AvroUserChangedEmailAction>();
        factory.setConsumerFactory(userChangedEmailActionConsumerFactory());
        factory.getContainerProperties().setPollTimeout(1000);
        return factory;
    }

    @Bean
    public ConsumerFactory<String, AvroInvoiceAction> invoiceActionConsumerFactory(){
        return new DefaultKafkaConsumerFactory<String,AvroInvoiceAction>(consumerConfig());
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String,AvroInvoiceAction> kafkaInvoiveActionListener(){
        ConcurrentKafkaListenerContainerFactory<String,AvroInvoiceAction> factory=new ConcurrentKafkaListenerContainerFactory<String,AvroInvoiceAction>();
        factory.setConsumerFactory(invoiceActionConsumerFactory());
        factory.getContainerProperties().setPollTimeout(1000);
        return factory;
    }

    @Bean
    public ConsumerFactory<String, AvroUserBookExtendAction> userBookExtendActionConsumerFactory(){
        return new DefaultKafkaConsumerFactory<String,AvroUserBookExtendAction>(consumerConfig());
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String,AvroUserBookExtendAction> kafkaUserBookExtendActionListener(){
        ConcurrentKafkaListenerContainerFactory<String, AvroUserBookExtendAction> factory=new ConcurrentKafkaListenerContainerFactory<String,AvroUserBookExtendAction>();
        factory.setConsumerFactory(userBookExtendActionConsumerFactory());
        factory.getContainerProperties().setPollTimeout(1000);
        return factory;
    }

    @Bean
    public ConsumerFactory<String, AvroBookAction> bookActionConsumerFactory(){
        return new DefaultKafkaConsumerFactory<String,AvroBookAction>(consumerConfig());
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String,AvroBookAction> kafkaBookActionListener(){
        ConcurrentKafkaListenerContainerFactory<String, AvroBookAction> factory=new ConcurrentKafkaListenerContainerFactory<String,AvroBookAction>();
        factory.setConsumerFactory(bookActionConsumerFactory());
        factory.getContainerProperties().setPollTimeout(1000);
        return factory;
    }


    @Bean
    public String userChangedEmailActionTopic(){
        return mainConfig.getKafkaUserChangedEmailActionTopic();
    }

    @Bean
    public String invoiceActionTopic(){
        return mainConfig.getKafkaInvoiceActionTopic();
    }

    @Bean
    public String userBookExtendActionTopic(){
        return mainConfig.getKafkaUserBookExtendActionTopic();
    }

    @Bean
    public String bookActionTopic(){
        return mainConfig.getKafkaBookActionTopic();
    }
}
