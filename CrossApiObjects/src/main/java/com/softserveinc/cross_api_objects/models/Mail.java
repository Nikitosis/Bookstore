package com.softserveinc.cross_api_objects.models;

public class Mail {
    private String receiverEmail;
    private String subject;
    private String body;
    private Attachment attachment;

    public Mail(String receiverEmail, String subject, String body, Attachment attachment) {
        this.receiverEmail = receiverEmail;
        this.subject = subject;
        this.body = body;
        this.attachment = attachment;
    }

    public Mail(){

    }

    public String getReceiverEmail() {
        return receiverEmail;
    }

    public void setReceiverEmail(String receiverEmail) {
        this.receiverEmail = receiverEmail;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Attachment getAttachment() {
        return attachment;
    }

    public void setAttachment(Attachment attachment) {
        this.attachment = attachment;
    }
}
