package com.softserveinc.invoicer.services;

import com.softserveinc.cross_api_objects.models.Attachment;
import com.softserveinc.cross_api_objects.models.Mail;
import com.softserveinc.cross_api_objects.models.User;
import com.softserveinc.cross_api_objects.services.storage.StoredFile;
import com.softserveinc.invoicer.services.request_senders.MailSenderService;
import com.softserveinc.invoicer.services.request_senders.RequestSenderKafkaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class InvoiceSenderService {
    private MailSenderService mailSenderService;

    @Autowired
    public InvoiceSenderService(RequestSenderKafkaService mailSenderService) {
        this.mailSenderService = mailSenderService;
    }

    public void sendInvoice(User user, String fileUrl){
        mailSenderService.sendEmail(createMailInvoice(user,fileUrl));
    }

    private Mail createMailInvoice(User user, String fileUrl){
        Mail mail=new Mail();
        mail.setReceiverEmail(user.getEmail());
        mail.setSubject("Bookstore invoice");
        mail.setBody("Invoice is attached");
        mail.setAttachment(new Attachment("Invoice.pdf",fileUrl));
        return mail;
    }
}
