package com.models;

import java.sql.Date;

public class ClientBookLog {
    private Long id;
    private Long clientId;
    private Long bookId;
    private Date startDate;
    private Date endDate;

    public ClientBookLog(){
        
    }

    public ClientBookLog(Long id, Long clientId, Long bookId, Date startDate, Date endDate) {
        this.id = id;
        this.clientId = clientId;
        this.bookId = bookId;
        this.startDate = startDate;
        this.endDate = endDate;
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

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
}
