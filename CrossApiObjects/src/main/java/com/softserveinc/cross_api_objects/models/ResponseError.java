package com.softserveinc.cross_api_objects.models;

public class ResponseError {
    private Integer code;
    private String message;

    public Integer getCode() {
        return code;
    }

    public ResponseError setCode(Integer code) {
        this.code = code;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public ResponseError setMessage(String message) {
        this.message = message;
        return this;
    }
}
