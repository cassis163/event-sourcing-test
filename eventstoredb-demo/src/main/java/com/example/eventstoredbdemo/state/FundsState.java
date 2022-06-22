package com.example.eventstoredbdemo.state;

import com.example.eventstoredbdemo.event.FundsDepositedEvent;
import com.example.eventstoredbdemo.event.FundsWithdrawnEvent;

public class FundsState {
    private float amount = 0;

    public void apply(FundsDepositedEvent event) {
        this.amount += event.getAmount();
    }

    public void apply(FundsWithdrawnEvent event) {
        this.amount += event.getAmount();
    }

    public float getAmount() {
        return this.amount;
    }
}
