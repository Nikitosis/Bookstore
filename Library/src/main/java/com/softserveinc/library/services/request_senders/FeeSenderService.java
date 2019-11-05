package com.softserveinc.library.services.request_senders;

public interface FeeSenderService {
    public void postChargeBookFee(Long userId,Long bookId);
}
