package com.softserveinc.invoicer.models;

import com.softserveinc.cross_api_objects.api.UserBookPaymentLog;

import java.util.List;

public class UserPayments {
    private Long userId;
    private List<UserBookPaymentLog> paymentLogs;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public List<UserBookPaymentLog> getPaymentLogs() {
        return paymentLogs;
    }

    public void setPaymentLogs(List<UserBookPaymentLog> paymentLogs) {
        this.paymentLogs = paymentLogs;
    }
}
