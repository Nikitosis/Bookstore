package com.softserveinc.invoicer.services;

import com.softserveinc.cross_api_objects.models.User;
import com.softserveinc.invoicer.services.request_senders.RequestSenderKafkaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class InvoiceSenderService {
    private RequestSenderKafkaService requestSenderKafkaService;

    @Autowired
    public InvoiceSenderService(RequestSenderKafkaService requestSenderKafkaService) {
        this.requestSenderKafkaService = requestSenderKafkaService;
    }

    public void sendInvoice(User user, String fileUrl){
        requestSenderKafkaService.sendInvoiceAction(user.getId(),fileUrl);
    }

}
