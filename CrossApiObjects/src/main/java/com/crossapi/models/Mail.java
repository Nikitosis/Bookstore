package com.crossapi.models;

public class Mail {
    private String receiverEmail;
    private String subject;
    private String body;

    public Mail(String receiverEmail, String subject, String body) {
        this.receiverEmail = receiverEmail;
        this.subject = subject;
        this.body = body;
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
}
