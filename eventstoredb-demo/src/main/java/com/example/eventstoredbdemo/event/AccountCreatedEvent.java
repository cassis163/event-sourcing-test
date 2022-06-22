package com.example.eventstoredbdemo.event;

public class AccountCreatedEvent implements Event {
    private final int id;

    public AccountCreatedEvent(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }
}
