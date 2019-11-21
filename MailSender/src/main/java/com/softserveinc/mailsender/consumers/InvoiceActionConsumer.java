package com.softserveinc.mailsender.consumers;

import com.softserveinc.cross_api_objects.avro.AvroInvoiceAction;
import com.softserveinc.cross_api_objects.models.Attachment;
import com.softserveinc.cross_api_objects.models.Mail;
import com.softserveinc.cross_api_objects.utils.correlation_id.CorrelationConstraints;
import com.softserveinc.cross_api_objects.utils.correlation_id.CorrelationManager;
import com.softserveinc.mailsender.dao.UserDao;
import com.softserveinc.mailsender.services.MailSenderService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

@Service
public class InvoiceActionConsumer {
    private static final Logger log= LoggerFactory.getLogger(InvoiceActionConsumer.class);

    private MailSenderService mailSenderService;
    private UserDao userDao;

    @Autowired
    public InvoiceActionConsumer(MailSenderService mailSenderService, UserDao userDao) {
        this.mailSenderService = mailSenderService;
        this.userDao = userDao;
    }

    @KafkaListener(topics = "#{@invoiceActionTopic}",containerFactory = "kafkaInvoiveActionListener")
    public void consume(ConsumerRecord<String, AvroInvoiceAction> record,
                        @Header(CorrelationConstraints.CORRELATION_ID_HEADER_NAME) String correlationId){
        CorrelationManager.setCorrelationId(correlationId);

        log.info("Consuming InvoiceAction. UserId: "+record.value().getUserId()+". Invoice link: "+record.value().getInvoice().getAttachmentUrl());

        Attachment attachment=new Attachment();
        attachment.setAttachmentName(record.value().getInvoice().getAttachmentName().toString());
        attachment.setAttachmentUrl(record.value().getInvoice().getAttachmentUrl().toString());

        Mail mail=new Mail();
        mail.setReceiverEmail(userDao.findById(record.value().getUserId()).getEmail());
        mail.setSubject("Bookstore invoice");
        mail.setBody("Your invoice is in the attachment");
        mail.setAttachment(attachment);

        if(userDao.findById(record.value().getUserId()).getEmailVerified()) {
            mailSenderService.sendMail(mail);
        }
        else{
            log.info("User's email is not verified. Don't send email.");
        }

        CorrelationManager.removeCorrelationId();
    }
}
