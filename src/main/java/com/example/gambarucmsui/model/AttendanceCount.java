package com.example.gambarucmsui.model;

import java.time.LocalDate;
import java.util.Date;

public class AttendanceCount {
    private LocalDate date;
    private long count;

    public AttendanceCount(LocalDate date, long count) {
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