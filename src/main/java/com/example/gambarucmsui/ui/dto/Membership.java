package com.example.gambarucmsui.ui.dto;

import java.time.LocalDateTime;

import static com.example.gambarucmsui.util.FormatUtil.toFullDateTime;

public class Membership {
    private String timestamp;
    public Membership(LocalDateTime timestamp) {
        this.timestamp = toFullDateTime(timestamp);
    }
    public String getTimestamp() {
        return timestamp;
    }
}
