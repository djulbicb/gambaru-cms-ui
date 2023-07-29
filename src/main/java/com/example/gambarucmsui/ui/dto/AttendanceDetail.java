package com.example.gambarucmsui.ui.dto;

import java.time.LocalDateTime;

import static com.example.gambarucmsui.util.FormatUtil.toDateTimeFormat;

public class AttendanceDetail {
    private String timestamp;

    public AttendanceDetail(LocalDateTime timestamp) {
        this.timestamp = toDateTimeFormat(timestamp);
    }

    public String getTimestamp() {
        return timestamp;
    }
}
