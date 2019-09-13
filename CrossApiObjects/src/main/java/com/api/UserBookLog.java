package com.api;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import java.time.LocalDateTime;
import java.util.Objects;

public class UserBookLog {

    private Long id;
    private String userId;
    private Long bookId;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime actionDate;
    private Action action;

    public UserBookLog(){

    }

    public UserBookLog(Long id, String userId, Long bookId, LocalDateTime actionDate, Action action) {
        this.id = id;
        this.userId = userId;
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }

    public LocalDateTime getActionDate() {
        return actionDate;
    }

    public void setActionDate(LocalDateTime actionDate) {
        this.actionDate = actionDate;
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    @Override
    public boolean equals(Object obj)
    {
        if(this == obj)
            return true;

        if(obj == null || obj.getClass()!= this.getClass())
            return false;

        UserBookLog log = (UserBookLog) obj;

        return Objects.equals(log.id,this.id) &&
                Objects.equals(log.userId,this.userId) &&
                Objects.equals(log.bookId,this.bookId) &&
                Objects.equals(log.actionDate,this.actionDate) &&
                Objects.equals(log.action,this.action);
    }
}