package com.softserveinc.mailsender.configuration;

import com.softserveinc.cross_api_objects.avro.AvroInvoiceAction;
import com.softserveinc.cross_api_objects.avro.AvroMail;
import com.softserveinc.cross_api_objects.avro.AvroUserChangedEmailAction;
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
    public ConsumerFactory<String, AvroMail> consumerFactory(){
        return new DefaultKafkaConsumerFactory<String, AvroMail>(consumerConfig());
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String,AvroMail> kafkaListenerContainerFactory(){
        ConcurrentKafkaListenerContainerFactory<String,AvroMail> factory=new ConcurrentKafkaListenerContainerFactory<String,AvroMail>();
        factory.setConsumerFactory(consumerFactory());
        factory.getContainerProperties().setPollTimeout(1000);
        return factory;
    }

    @Bean
    public String mailTopic(){
        return mainConfig.getKafkaMailTopic();
    }

    @Bean
    public String userChangedEmailActionTopic(){
        return mainConfig.getKafkaUserChangedEmailActionTopic();
    }

    @Bean
    public String invoiceActionTopic(){
        return mainConfig.getKafkaInvoiceActionTopic();
    }
}
