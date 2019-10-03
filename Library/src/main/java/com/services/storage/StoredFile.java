package com.services.storage;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import java.io.InputStream;

public class StoredFile {
    private InputStream inputStream;
    private String fileName;

    public StoredFile(){

    }

    public StoredFile(InputStream inputStream, String fileName) {
        this.inputStream = inputStream;
        this.fileName = fileName;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
