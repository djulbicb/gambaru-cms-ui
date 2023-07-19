package com.example.gambarucmsui;

import java.time.LocalDateTime;

public class Membership {
    private LocalDateTime timestamp;

    public Membership(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
