package com.softserveinc.invoicer.services.request_senders;

import com.softserveinc.cross_api_objects.models.Mail;

public interface MailSenderService {
    public void sendEmail(Mail mail);
}
