package com.softserveinc.mailsender.services;

import javax.mail.internet.MimeMessage;

public interface MailSenderService {
    public void sendMail(MimeMessage mail);
}
