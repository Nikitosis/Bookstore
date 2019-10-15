package com.softserveinc.mailsender.services;

import com.softserveinc.cross_api_objects.models.Mail;

public interface MailSenderService {
    public void sendMail(Mail mail);
}
