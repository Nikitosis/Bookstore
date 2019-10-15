package com.softserveinc.feecharger.models;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class UserBook {
    private Long userId;
    private Long bookId;
    private LocalDate takeDate;
    private LocalDate returnDate;
    private LocalDateTime paidUntil;

    public UserBook(){

    }

    public UserBook(Long userId, Long bookId, LocalDate takeDate, LocalDate returnDate, LocalDateTime paidUntil) {
        this.userId = userId;
        this.bookId = bookId;
        this.takeDate = takeDate;
        this.returnDate = returnDate;
        this.paidUntil = paidUntil;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }

    public LocalDate getTakeDate() {
        return takeDate;
    }

    public void setTakeDate(LocalDate takeDate) {
        this.takeDate = takeDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    public LocalDateTime getPaidUntil() {
        return paidUntil;
    }

    public void setPaidUntil(LocalDateTime paidUntil) {
        this.paidUntil = paidUntil;
    }
}
