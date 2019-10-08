package com.resources;

import com.crossapi.models.Mail;
import com.services.MailSenderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger log= LoggerFactory.getLogger(MailSenderResource.class);

    private MailSenderService mailSenderService;

    @Autowired
    public MailSenderResource(MailSenderService mailSenderService) {
        this.mailSenderService = mailSenderService;
    }

    @POST
    @Path("/mail")
    public void sendMail(@Valid Mail mail){
        log.info("Sending mail to "+mail.getReceiverEmail());
        mailSenderService.sendMail(mail);
    }
}
