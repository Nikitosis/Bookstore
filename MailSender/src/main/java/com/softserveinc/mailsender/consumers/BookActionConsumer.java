package com.softserveinc.mailsender.consumers;

import com.softserveinc.cross_api_objects.avro.AvroBookAction;
import com.softserveinc.cross_api_objects.avro.AvroBookActionStatus;
import com.softserveinc.cross_api_objects.utils.correlation_id.CorrelationConstraints;
import com.softserveinc.cross_api_objects.utils.correlation_id.CorrelationManager;
import com.softserveinc.mailsender.dao.BookDao;
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
public class BookActionConsumer {
    private static final Logger log= LoggerFactory.getLogger(InvoiceActionConsumer.class);

    private MailSenderService mailSenderService;
    private BookDao bookDao;
    private UserDao userDao;

    @Autowired
    public BookActionConsumer(MailSenderService mailSenderService, BookDao bookDao, UserDao userDao) {
        this.mailSenderService = mailSenderService;
        this.bookDao = bookDao;
        this.userDao = userDao;
    }

    @KafkaListener(topics = "#{@bookActionTopic}",containerFactory = "kafkaBookActionListener")
    public void consume(ConsumerRecord<String, AvroBookAction> record,
                        @Header(CorrelationConstraints.CORRELATION_ID_HEADER_NAME) String correlationId){
        CorrelationManager.setCorrelationId(correlationId);

        log.info("Consuming AvroBookAction. Book id: "+record.value().getBookId()+". Status: "+record.value().getStatus().toString());

//        if(record.value().getStatus()== AvroBookActionStatus.CREATED){
//
//        }

        CorrelationManager.removeCorrelationId();
    }
}
