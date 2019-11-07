package com.softserveinc.cross_api_objects.models;

public class Attachment {
    private String attachmentName;
    private String attachmentUrl;

    public Attachment(){

    }

    public Attachment(String attachmentName, String attachmentUrl) {
        this.attachmentName = attachmentName;
        this.attachmentUrl = attachmentUrl;
    }

    public String getAttachmentName() {
        return attachmentName;
    }

    public void setAttachmentName(String attachmentName) {
        this.attachmentName = attachmentName;
    }

    public String getAttachmentUrl() {
        return attachmentUrl;
    }

    public void setAttachmentUrl(String attachmentUrl) {
        this.attachmentUrl = attachmentUrl;
    }
}
