package com.softserveinc.feecharger.services.request_senders;

import com.softserveinc.cross_api_objects.api.UserBookPaymentLog;
import com.softserveinc.feecharger.models.UserBook;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface LogSenderService {
    public void sendPaymentLog(UserBookPaymentLog userBookPaymentLog);
}
