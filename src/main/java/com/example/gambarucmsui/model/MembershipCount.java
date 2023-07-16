package com.example.gambarucmsui.model;

import java.time.LocalDate;

public class MembershipCount {
    private LocalDate date;
    private long count;

    public MembershipCount(LocalDate date, long count) {
        this.date = date;
        this.count = count;
    }

    public LocalDate getDate() {
        return date;
    }

    public long getCount() {
        return count;
    }
}