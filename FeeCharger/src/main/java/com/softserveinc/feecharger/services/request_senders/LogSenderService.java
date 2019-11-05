package com.softserveinc.feecharger.services.request_senders;

import com.softserveinc.feecharger.models.UserBook;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface LogSenderService {
    public void sendPaymentLog(UserBook rent, BigDecimal payment, LocalDateTime dateTime);
}
