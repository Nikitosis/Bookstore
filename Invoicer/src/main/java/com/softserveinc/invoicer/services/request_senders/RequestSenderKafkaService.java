package com.softserveinc.invoicer.services.request_senders;

import com.softserveinc.cross_api_objects.avro.*;
import com.softserveinc.cross_api_objects.models.Mail;
import com.softserveinc.invoicer.MainConfig;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("requestSenderKafkaService")
public class RequestSenderKafkaService{
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

       avroInvoiceProducer.send(new ProducerRecord<String,AvroInvoiceAction>(
               mainConfig.getKafkaInvoiceActionTopic(),
               userId.toString(),
               avroInvoiceAction
       ));
    }

}
