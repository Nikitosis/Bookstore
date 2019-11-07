package com.softserveinc.cross_api_objects.avro;


import com.softserveinc.cross_api_objects.api.Action;
import com.softserveinc.cross_api_objects.api.UserBookLog;
import com.softserveinc.cross_api_objects.api.UserBookPaymentLog;
import com.softserveinc.cross_api_objects.models.Attachment;
import com.softserveinc.cross_api_objects.models.Mail;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

public class AvroConverter {
    public static final AvroMail buildAvroMail(Mail mail){
        return new AvroMail.Builder()
                .setBody(mail.getBody())
                .setSubject(mail.getSubject())
                .setReceiverEmaill(mail.getReceiverEmail())
                .setAttachment(buildAvroAttachment(mail.getAttachment()))
                .build();
    }

    public static final Mail buildMail(AvroMail avroMail){
        Mail mail=new Mail();
        mail.setSubject(avroMail.getSubject().toString());
        mail.setReceiverEmail(avroMail.getReceiverEmaill().toString());
        mail.setBody(avroMail.getBody().toString());
        mail.setAttachment(buildAttachment(avroMail.getAttachment()));
        return mail;
    }

    public static final AvroAttachment buildAvroAttachment(Attachment attachment){
        return new AvroAttachment.Builder()
                .setAttachmentName(attachment.getAttachmentName())
                .setAttachmentUrl(attachment.getAttachmentUrl())
                .build();
    }

    public static final Attachment buildAttachment(AvroAttachment avroAttachment){
        Attachment attachment=new Attachment();
        attachment.setAttachmentUrl(avroAttachment.getAttachmentUrl().toString());
        attachment.setAttachmentName(avroAttachment.getAttachmentName().toString());
        return attachment;
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
