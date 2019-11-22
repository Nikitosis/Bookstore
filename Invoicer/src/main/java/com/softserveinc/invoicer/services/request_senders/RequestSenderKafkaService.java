package com.softserveinc.invoicer.services.request_senders;

import com.softserveinc.cross_api_objects.avro.*;
import com.softserveinc.cross_api_objects.utils.correlation_id.CorrelationConstraints;
import com.softserveinc.cross_api_objects.utils.correlation_id.CorrelationManager;
import com.softserveinc.invoicer.MainConfig;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("requestSenderKafkaService")
public class RequestSenderKafkaService{
    private static final Logger log= LoggerFactory.getLogger(RequestSenderKafkaService.class);

    private MainConfig mainConfig;
    private Producer<String, AvroInvoiceAction> avroInvoiceProducer;

    @Autowired
    public RequestSenderKafkaService(MainConfig mainConfig, Producer<String, AvroInvoiceAction> avroInvoiceProducer) {
        this.mainConfig = mainConfig;
        this.avroInvoiceProducer = avroInvoiceProducer;
    }

    public void sendInvoiceAction(Long userId, String invoiceUrl) {
        AvroAttachment avroAttachment=AvroAttachment.newBuilder()
                .setAttachmentName("Invoice.pdf")
                .setAttachmentUrl(invoiceUrl)
                .build();

        AvroInvoiceAction avroInvoiceAction=AvroInvoiceAction.newBuilder()
                .setUserId(userId)
                .setInvoice(avroAttachment)
                .build();

        ProducerRecord<String,AvroInvoiceAction> record=new ProducerRecord<String,AvroInvoiceAction>(
                mainConfig.getKafkaInvoiceActionTopic(),
                userId.toString(),
                avroInvoiceAction
        );

        record.headers().add(CorrelationConstraints.CORRELATION_ID_HEADER_NAME, CorrelationManager.getCorrelationId().getBytes());

        log.info("Sending InvoiceAction. UserId: "+record.value().getUserId()+". Invoice link: "+record.value().getInvoice().getAttachmentUrl());
        avroInvoiceProducer.send(record);
    }

}
