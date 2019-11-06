package com.softserveinc.cross_api_objects.models;

import java.math.BigDecimal;

public class InvoiceData {
    String userName;
    String userSurname;
    String bookName;
    String payment;

    public InvoiceData(){

    }

    public InvoiceData(String userName, String userSurname, String bookName, String payment) {
        this.userName = userName;
        this.userSurname = userSurname;
        this.bookName = bookName;
        this.payment = payment;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserSurname() {
        return userSurname;
    }

    public void setUserSurname(String userSurname) {
        this.userSurname = userSurname;
    }

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

}
