package com.softserveinc.feecharger.services.request_senders;

import com.softserveinc.cross_api_objects.models.Book;
import com.softserveinc.cross_api_objects.models.Mail;
import com.softserveinc.cross_api_objects.models.User;
import com.softserveinc.feecharger.models.UserBook;

public interface MailSenderService {
    public void sendEmail(Mail mail);
}
