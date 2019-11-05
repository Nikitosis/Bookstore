package com.softserveinc.cross_api_objects.avro;


import com.softserveinc.cross_api_objects.api.Action;
import com.softserveinc.cross_api_objects.api.UserBookLog;
import com.softserveinc.cross_api_objects.models.Mail;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

public class AvroConverter {
    public static final AvroMail buildAvroMail(Mail mail){
        return new AvroMail.Builder()
                .setBody(mail.getBody())
                .setReceiverEmaill(mail.getReceiverEmail())
                .setSubject(mail.getSubject())
                .build();
    }
    public static final Mail buildMail(AvroMail avroMail){
        Mail mail=new Mail();
        mail.setSubject(avroMail.getSubject().toString());
        mail.setReceiverEmail(avroMail.getReceiverEmaill().toString());
        mail.setBody(avroMail.getBody().toString());
        return mail;
    }

    public static final AvroUserBookLog buildAvroUserBookLog(UserBookLog userBookLog){
        return new AvroUserBookLog.Builder()
                .setId(userBookLog.getId())
                .setAction(AvroAction.valueOf(userBookLog.getAction().toString()))
                .setBookId(userBookLog.getBookId())
                .setUserId(userBookLog.getUserId())
                .setDate(userBookLog.getDate().toInstant(ZoneOffset.UTC).toEpochMilli())
                .build();
    }

    public static final UserBookLog buildUserBookLog(AvroUserBookLog userBookLog){
        return new com.softserveinc.cross_api_objects.api.UserBookLog(
                userBookLog.getId(),
                userBookLog.getUserId(),
                userBookLog.getBookId(),
                Instant.ofEpochMilli(userBookLog.getDate()).atZone(ZoneId.systemDefault()).toLocalDateTime(),
                com.softserveinc.cross_api_objects.api.Action.valueOf(userBookLog.getAction().toString())
        );
    }
}
