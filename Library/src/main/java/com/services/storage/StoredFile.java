package com.services.storage;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import java.io.InputStream;

public class StoredFile {
    private InputStream inputStream;
    private FormDataContentDisposition contentDisposition;

    public StoredFile(InputStream inputStream, FormDataContentDisposition contentDisposition) {
        this.inputStream = inputStream;
        this.contentDisposition = contentDisposition;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public FormDataContentDisposition getContentDisposition() {
        return contentDisposition;
    }

    public void setContentDisposition(FormDataContentDisposition contentDisposition) {
        this.contentDisposition = contentDisposition;
    }
}
