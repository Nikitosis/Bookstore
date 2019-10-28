package com.softserveinc.library.models;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

public class Deposit {
    @NotNull
    BigDecimal money;

    public BigDecimal getMoney() {
        return money;
    }

    public void setMoney(BigDecimal money) {
        this.money = money;
    }
}
