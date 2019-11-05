package com.softserveinc.cross_api_objects.avro;


import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

public class AvroConverter {
    public static final Mail buildAvroMail(com.softserveinc.cross_api_objects.models.Mail mail){
        return new Mail.Builder()
                .setBody(mail.getBody())
                .setReceiverEmaill(mail.getReceiverEmail())
                .setSubject(mail.getSubject())
                .build();
    }
    public static final com.softserveinc.cross_api_objects.models.Mail buildMail(Mail avroMail){
        com.softserveinc.cross_api_objects.models.Mail mail=new com.softserveinc.cross_api_objects.models.Mail();
        mail.setSubject(avroMail.getSubject().toString());
        mail.setReceiverEmail(avroMail.getReceiverEmaill().toString());
        mail.setBody(avroMail.getBody().toString());
        return mail;
    }

    public static final UserBookLog buildAvroUserBookLog(com.softserveinc.cross_api_objects.api.UserBookLog userBookLog){
        return new UserBookLog.Builder()
                .setId(userBookLog.getId())
                .setAction(Action.valueOf(userBookLog.getAction().toString()))
                .setBookId(userBookLog.getBookId())
                .setUserId(userBookLog.getUserId())
                .setDate(userBookLog.getDate().toInstant(ZoneOffset.UTC).toEpochMilli())
                .build();
    }

    public static final com.softserveinc.cross_api_objects.api.UserBookLog buildUserBookLog(UserBookLog userBookLog){
        return new com.softserveinc.cross_api_objects.api.UserBookLog(
                userBookLog.getId(),
                userBookLog.getUserId(),
                userBookLog.getBookId(),
                Instant.ofEpochMilli(userBookLog.getDate()).atZone(ZoneId.systemDefault()).toLocalDateTime(),
                com.softserveinc.cross_api_objects.api.Action.valueOf(userBookLog.getAction().toString())
        );
    }
}
