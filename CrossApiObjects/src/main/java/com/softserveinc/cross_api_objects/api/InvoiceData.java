package com.softserveinc.cross_api_objects.api;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.softserveinc.cross_api_objects.models.InvoiceSinglePaymentData;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class InvoiceData {
    @NotNull
    String userName;

    @NotNull
    String userSurname;

    @NotNull
    List<InvoiceSinglePaymentData> invoiceSinglePaymentData;

    public InvoiceData(){

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

    public List<InvoiceSinglePaymentData> getInvoiceSinglePaymentData() {
        return invoiceSinglePaymentData;
    }

    public void setInvoiceSinglePaymentData(List<InvoiceSinglePaymentData> invoiceSinglePaymentData) {
        this.invoiceSinglePaymentData = invoiceSinglePaymentData;
    }
}
