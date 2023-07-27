package com.example.gambarucmsui.ui.dto;

import java.time.LocalDateTime;

import static com.example.gambarucmsui.util.FormatUtil.toFullDateTime;

public class MembershipDetail {
    private String timestamp;
    public MembershipDetail(LocalDateTime timestamp) {
        this.timestamp = toFullDateTime(timestamp);
    }
    public String getTimestamp() {
        return timestamp;
    }
}
