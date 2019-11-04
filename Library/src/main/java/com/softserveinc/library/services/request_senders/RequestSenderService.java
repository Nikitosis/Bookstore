package com.softserveinc.library.services.request_senders;

import com.softserveinc.cross_api_objects.api.UserBookLog;
import com.softserveinc.cross_api_objects.models.Mail;

public interface RequestSenderService {
    public void postUserBookLog(UserBookLog userBookLog);
    public void postChargeBookFee(Long userId,Long bookId);
    public void sendEmailVerification(Mail mail);
}
