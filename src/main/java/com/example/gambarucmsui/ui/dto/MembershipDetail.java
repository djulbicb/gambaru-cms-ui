package com.example.gambarucmsui.ui.dto;

import java.time.LocalDateTime;

import static com.example.gambarucmsui.util.FormatUtil.toDateTimeFormat;

public class MembershipDetail {
    private String timestamp;
    public MembershipDetail(LocalDateTime timestamp) {
        this.timestamp = toDateTimeFormat(timestamp);
    }
    public String getTimestamp() {
        return timestamp;
    }
}
