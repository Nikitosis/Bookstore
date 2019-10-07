package com.configuration;

public class FeeChargeConfig {
    private Long rentPeriod;
    private Long checkExpirationInterval;

    public Long getRentPeriod() {
        return rentPeriod;
    }

    public void setRentPeriod(Long rentPeriod) {
        this.rentPeriod = rentPeriod;
    }

    public Long getCheckExpirationInterval() {
        return checkExpirationInterval;
    }

    public void setCheckExpirationInterval(Long checkExpirationInterval) {
        this.checkExpirationInterval = checkExpirationInterval;
    }
}
