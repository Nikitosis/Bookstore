package com.softserveinc.mailsender.consumers;

import com.softserveinc.cross_api_objects.avro.AvroUserBookAction;
import com.softserveinc.cross_api_objects.avro.AvroUserBookExtendAction;
import com.softserveinc.cross_api_objects.avro.AvroUserBookExtendActionStatus;
import com.softserveinc.cross_api_objects.avro.AvroUserBookPaymentAction;
import com.softserveinc.cross_api_objects.models.Book;
import com.softserveinc.cross_api_objects.models.Mail;
import com.softserveinc.cross_api_objects.models.User;
import com.softserveinc.cross_api_objects.utils.correlation_id.CorrelationConstraints;
import com.softserveinc.cross_api_objects.utils.correlation_id.CorrelationManager;
import com.softserveinc.mailsender.dao.BookDao;
import com.softserveinc.mailsender.dao.UserDao;
import com.softserveinc.mailsender.services.MailSenderService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

@Service
public class UserBookPaymentActionConsumer {
    private MailSenderService mailSenderService;
    private UserDao userDao;
    private BookDao bookDao;

    @Autowired
    public UserBookPaymentActionConsumer(MailSenderService mailSenderService, UserDao userDao, BookDao bookDao) {
        this.mailSenderService = mailSenderService;
        this.userDao = userDao;
        this.bookDao = bookDao;
    }

    @KafkaListener(topics = "#{@userBookExtendActionTopic}", containerFactory = "kafkaUserBookExtendActionListener")
    public void consume(ConsumerRecord<String, AvroUserBookExtendAction> record,
                        @Header(CorrelationConstraints.CORRELATION_ID_HEADER_NAME) String correlationId){
        CorrelationManager.setCorrelationId(correlationId);

        if(record.value().getStatus()==AvroUserBookExtendActionStatus.NOT_ENOUGH_MONEY) {

            User user = userDao.findById(record.value().getUserId());
            Book book = bookDao.findById(record.value().getBookId());

            Mail mail = new Mail();
            mail.setReceiverEmail(user.getEmail());
            mail.setSubject("Cannot extend book");
            mail.setBody("Unfortunately, cannot extend book " + book.getName() +
                    ". You only have " + user.getMoney() + "$, but book costs " + book.getPrice() + "$");

            mailSenderService.sendMail(mail);

            CorrelationManager.removeCorrelationId();
        }
    }
}
