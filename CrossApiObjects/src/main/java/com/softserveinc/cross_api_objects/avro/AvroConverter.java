package com.softserveinc.cross_api_objects.avro;


import com.softserveinc.cross_api_objects.api.Action;
import com.softserveinc.cross_api_objects.api.UserBookLog;
import com.softserveinc.cross_api_objects.api.UserBookPaymentLog;
import com.softserveinc.cross_api_objects.models.Mail;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

public class AvroConverter {
    public static final AvroMail buildAvroMail(Mail mail){
        return new AvroMail(
                mail.getReceiverEmail(),
                mail.getSubject(),
                mail.getBody()
        );
    }
    public static final Mail buildMail(AvroMail avroMail){
        Mail mail=new Mail();
        mail.setSubject(avroMail.getSubject().toString());
        mail.setReceiverEmail(avroMail.getReceiverEmaill().toString());
        mail.setBody(avroMail.getBody().toString());
        return mail;
    }

    public static final AvroUserBookLog buildAvroUserBookLog(UserBookLog userBookLog){
        return new AvroUserBookLog(
                userBookLog.getId(),
                userBookLog.getUserId(),
                userBookLog.getBookId(),
                userBookLog.getDate().toInstant(ZoneOffset.UTC).toEpochMilli(),
                AvroAction.valueOf(userBookLog.getAction().toString())
        );
    }

    public static final UserBookLog buildUserBookLog(AvroUserBookLog avroUserBookLog){
        return new UserBookLog(
                avroUserBookLog.getId(),
                avroUserBookLog.getUserId(),
                avroUserBookLog.getBookId(),
                Instant.ofEpochMilli(avroUserBookLog.getDate()).atZone(ZoneId.systemDefault()).toLocalDateTime(),
                Action.valueOf(avroUserBookLog.getAction().toString())
        );
    }

    public static final AvroUserBookPaymentLog buildAvroUserBookPaymentLog(UserBookPaymentLog userBookPaymentLog){
        return new AvroUserBookPaymentLog(
                userBookPaymentLog.getId(),
                userBookPaymentLog.getUserId(),
                userBookPaymentLog.getBookId(),
                userBookPaymentLog.getDate().toInstant(ZoneOffset.UTC).toEpochMilli(),
                userBookPaymentLog.getPayment().toString()
        );
    }

    public static final UserBookPaymentLog buildUserBookPaymentLog(AvroUserBookPaymentLog avroUserBookPaymentLog){
        return new UserBookPaymentLog(
                avroUserBookPaymentLog.getId(),
                avroUserBookPaymentLog.getUserId(),
                avroUserBookPaymentLog.getBookId(),
                Instant.ofEpochMilli(avroUserBookPaymentLog.getDate()).atZone(ZoneId.systemDefault()).toLocalDateTime(),
                new BigDecimal(avroUserBookPaymentLog.getPayment().toString())
        );
    }
}