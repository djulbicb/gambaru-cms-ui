package com.example.gambarucmsui;

import java.time.LocalDateTime;

public class Attendance {
    private LocalDateTime timestamp;

    public Attendance(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
