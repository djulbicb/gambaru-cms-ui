package com.example.gambarucmsui.ui.dto;

import java.time.LocalDateTime;

import static com.example.gambarucmsui.util.FormatUtil.toFullDateTime;

public class Attendance {
    private String timestamp;

    public Attendance(LocalDateTime timestamp) {
        this.timestamp = toFullDateTime(timestamp);
    }

    public String getTimestamp() {
        return timestamp;
    }
}
