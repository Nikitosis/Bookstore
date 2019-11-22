package com.softserveinc.mailsender.consumers;

import com.softserveinc.cross_api_objects.avro.AvroBookAction;
import com.softserveinc.cross_api_objects.avro.AvroBookActionStatus;
import com.softserveinc.cross_api_objects.avro.AvroUserChangedEmailAction;
import com.softserveinc.cross_api_objects.models.User;
import com.softserveinc.cross_api_objects.utils.correlation_id.CorrelationConstraints;
import com.softserveinc.cross_api_objects.utils.correlation_id.CorrelationManager;
import com.softserveinc.mailsender.MainConfig;
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

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

@Service
public class BookActionConsumer {
    private static final Logger log= LoggerFactory.getLogger(InvoiceActionConsumer.class);

    private MailSenderService mailSenderService;
    private BookDao bookDao;
    private UserDao userDao;
    private MainConfig mainConfig;
    private Session session;

    @Autowired
    public BookActionConsumer(MailSenderService mailSenderService, BookDao bookDao, UserDao userDao, MainConfig mainConfig, Session session) {
        this.mailSenderService = mailSenderService;
        this.bookDao = bookDao;
        this.userDao = userDao;
        this.mainConfig = mainConfig;
        this.session = session;
    }

    @KafkaListener(topics = "#{@bookActionTopic}",containerFactory = "kafkaBookActionListener")
    public void consume(ConsumerRecord<String, AvroBookAction> record,
                        @Header(CorrelationConstraints.CORRELATION_ID_HEADER_NAME) String correlationId){
        CorrelationManager.setCorrelationId(correlationId);

        log.info("Consuming AvroBookAction. Book id: "+record.value().getBookId()+". Status: "+record.value().getStatus().toString());

        if(record.value().getStatus()== AvroBookActionStatus.CREATED){
            mailSenderService.sendMail(prepareMessage(record));
        }

        CorrelationManager.removeCorrelationId();
    }

    private MimeMessage prepareMessage(ConsumerRecord<String, AvroBookAction> record){
        MimeMessage message=new MimeMessage(session);
        try {
            //create whole body
            Multipart multipart=new MimeMultipart();
            //create body text
            MimeBodyPart textBodyPart=new MimeBodyPart();
            String html="<h2>"+
                    "Omg, new book is added!!"+
                    "</h2>"+
                    "<img src=\""+bookDao.findById(record.value().getBookId()).getPhotoLink()+"\">"+
                    "<p>"+
                    "Check out: "+
                    "<b>"+bookDao.findById(record.value().getBookId()).getName()+"</b>"+
                    "</p>";
            textBodyPart.setText(html,"UTF-8","html");

            multipart.addBodyPart(textBodyPart);

            //create message
            message.setFrom(new InternetAddress(mainConfig.getMailConfig().getFromAddress()));
            //set subscribers as recipients
            for(User user:userDao.findAllNewsSubscribers()){
                message.addRecipients(Message.RecipientType.TO,user.getEmail());
            }

            message.setSubject("Bookstore news");
            message.setContent(multipart,"text/html");

        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return message;
    }

}
