package com.example.gambarucmsui.ui.dto;

import java.time.LocalDateTime;

import static com.example.gambarucmsui.util.FormatUtil.toFullDateTime;

public class AttendanceDetail {
    private String timestamp;

    public AttendanceDetail(LocalDateTime timestamp) {
        this.timestamp = toFullDateTime(timestamp);
    }

    public String getTimestamp() {
        return timestamp;
    }
}
