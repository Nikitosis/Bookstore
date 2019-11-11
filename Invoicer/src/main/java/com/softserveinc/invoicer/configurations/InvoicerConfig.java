package com.softserveinc.invoicer.configurations;

public class InvoicerConfig {
    private Long generateInvoicePeriod;

    public Long getGenerateInvoicePeriod() {
        return generateInvoicePeriod;
    }

    public void setGenerateInvoicePeriod(Long generateInvoicePeriod) {
        this.generateInvoicePeriod = generateInvoicePeriod;
    }
}
