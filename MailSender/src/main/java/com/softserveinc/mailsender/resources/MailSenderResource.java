package com.softserveinc.mailsender.resources;

import com.softserveinc.cross_api_objects.models.Mail;
import com.softserveinc.mailsender.services.MailSenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

@Service
@Path("/")
@Consumes(MediaType.APPLICATION_JSON)
public class MailSenderResource {

    private MailSenderService mailSenderService;

    @Autowired
    public MailSenderResource(MailSenderService mailSenderService) {
        this.mailSenderService = mailSenderService;
    }

    @POST
    @Path("/mail")
    public void sendMail(@Valid Mail mail){
        mailSenderService.sendMail(mail);
    }
}