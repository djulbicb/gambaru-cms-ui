package com.example.gambarucmsui.ports.interfaces.attendance;

import com.example.gambarucmsui.ui.dto.statistics.AttendanceCount;

import java.time.LocalDate;
import java.util.List;

public interface GetAttendanceCountForMonth {
    public List<AttendanceCount> getAttendanceCount (LocalDate forDate);
}
