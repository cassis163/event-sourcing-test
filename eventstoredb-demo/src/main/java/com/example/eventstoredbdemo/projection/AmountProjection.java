package com.example.eventstoredbdemo.projection;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class AmountProjection {
    private final float amount;

    @JsonCreator
    public AmountProjection(@JsonProperty("amount") float amount) {
        this.amount = amount;
    }

    public float getAmount() {
        return this.amount;
    }
}
