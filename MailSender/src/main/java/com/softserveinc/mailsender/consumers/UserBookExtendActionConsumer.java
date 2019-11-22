package com.softserveinc.mailsender.consumers;

import com.softserveinc.cross_api_objects.avro.*;
import com.softserveinc.cross_api_objects.models.Book;
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

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

@Service
public class UserBookExtendActionConsumer {
    private static final Logger log= LoggerFactory.getLogger(InvoiceActionConsumer.class);

    private MailSenderService mailSenderService;
    private UserDao userDao;
    private BookDao bookDao;
    private MainConfig mainConfig;
    private Session session;

    @Autowired
    public UserBookExtendActionConsumer(MailSenderService mailSenderService, UserDao userDao, BookDao bookDao, MainConfig mainConfig, Session session) {
        this.mailSenderService = mailSenderService;
        this.userDao = userDao;
        this.bookDao = bookDao;
        this.mainConfig = mainConfig;
        this.session = session;
    }

    @KafkaListener(topics = "#{@userBookExtendActionTopic}", containerFactory = "kafkaUserBookExtendActionListener")
    public void consume(ConsumerRecord<String, AvroUserBookExtendAction> record,
                        @Header(CorrelationConstraints.CORRELATION_ID_HEADER_NAME) String correlationId){
        CorrelationManager.setCorrelationId(correlationId);

        log.info("Consuming UserBookExtendAction. User id: "+record.value().getUserId()+". Book id: "+
                record.value().getBookId()+". Status: "+record.value().getStatus().toString());

        if(record.value().getStatus()==AvroUserBookExtendActionStatus.NOT_ENOUGH_MONEY) {
            if(userDao.findById(record.value().getUserId()).getEmailVerified()) {
                mailSenderService.sendMail(prepareMessage(record));
            }
            else{
                log.info("User's email is not verified. Don't send email.");
            }
        }

        CorrelationManager.removeCorrelationId();
    }

    private MimeMessage prepareMessage(ConsumerRecord<String, AvroUserBookExtendAction> record){
        User user = userDao.findById(record.value().getUserId());
        Book book = bookDao.findById(record.value().getBookId());

        MimeMessage message=new MimeMessage(session);
        try {
            //create whole body
            Multipart multipart=new MimeMultipart();
            //create body text
            MimeBodyPart textBodyPart=new MimeBodyPart();
            String html="<p>"+
                    "Unfortunately, cannot extend book " +"<b>"+ book.getName() +"</b>"+". You only have " +
                    "<font color=\"green\">"+user.getMoney() + "$"+"</font>"+
                    " but book costs " +
                    "<font color=\"green\">"+book.getPrice() + "$"+"</font>"+
                    "</p>";
            textBodyPart.setText(html,"UTF-8","html");

            multipart.addBodyPart(textBodyPart);

            //create message
            message.setFrom(new InternetAddress(mainConfig.getMailConfig().getFromAddress()));
            message.setRecipients(
                    Message.RecipientType.TO,
                    user.getEmail()
            );
            message.setSubject("Cannot extend book");
            message.setContent(multipart,"text/html");

        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return message;
    }
}
