package com.softserveinc.logger.models;

import java.math.BigDecimal;

public class BookStatistics {
    private Long takenAmount;
    private Long returnedAmount;
    private BigDecimal totalPayments;

    public Long getTakenAmount() {
        return takenAmount;
    }

    public void setTakenAmount(Long takenAmount) {
        this.takenAmount = takenAmount;
    }

    public Long getReturnedAmount() {
        return returnedAmount;
    }

    public void setReturnedAmount(Long returnedAmount) {
        this.returnedAmount = returnedAmount;
    }

    public BigDecimal getTotalPayments() {
        return totalPayments;
    }

    public void setTotalPayments(BigDecimal totalPayments) {
        this.totalPayments = totalPayments;
    }
}
