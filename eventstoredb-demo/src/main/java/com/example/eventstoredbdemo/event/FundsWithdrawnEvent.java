package com.example.eventstoredbdemo.event;

public class FundsWithdrawnEvent implements Event {
    private final int bankAccountId;
    private final float amount;

    public FundsWithdrawnEvent(int bankAccountId, float amount) {
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
