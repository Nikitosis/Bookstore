package com.models;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.sql.Date;

public class ClientBookLog {

    private Long id;
    private Long clientId;
    private Long bookId;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private Date actionDate;
    private Action action;

    public ClientBookLog(){

    }

    public ClientBookLog(Long id, Long clientId, Long bookId, Date actionDate, Action action) {
        this.id = id;
        this.clientId = clientId;
        this.bookId = bookId;
        this.actionDate = actionDate;
        this.action = action;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }

    public Date getActionDate() {
        return actionDate;
    }

    public void setActionDate(Date actionDate) {
        this.actionDate = actionDate;
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }
}
