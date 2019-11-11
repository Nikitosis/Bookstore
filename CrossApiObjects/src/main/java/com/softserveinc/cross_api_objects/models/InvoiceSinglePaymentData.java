package com.softserveinc.cross_api_objects.models;

import javax.validation.constraints.NotNull;

public class InvoiceSinglePaymentData {
    @NotNull
    String bookName;

    @NotNull
    String payment;

    @NotNull
    String dateTime;

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getPayment() {
        return payment;
    }

    public void setPayment(String payment) {
        this.payment = payment;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }
}
