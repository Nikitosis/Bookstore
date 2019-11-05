package com.softserveinc.library.services.request_senders;

import com.softserveinc.cross_api_objects.api.UserBookLog;

public interface LogSenderService {
    public void sendUserBookLog(UserBookLog userBookLog);
}
