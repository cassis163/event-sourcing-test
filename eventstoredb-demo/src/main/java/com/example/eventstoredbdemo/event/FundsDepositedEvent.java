package com.example.eventstoredbdemo.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class FundsDepositedEvent implements Event {
    private final int bankAccountId;
    private final float amount;

    @JsonCreator
    public FundsDepositedEvent(@JsonProperty("bankAccountId") int bankAccountId, @JsonProperty("amount") float amount) {
        this.bankAccountId = bankAccountId;
        this.amount = amount;
    }

    public int getBankAccountId() {
        return this.bankAccountId;
    }

    public float getAmount() {
        return this.amount;
    }
}
