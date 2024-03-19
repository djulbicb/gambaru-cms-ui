package com.example.gambarucmsui.ports.interfaces.attendance;

import com.example.gambarucmsui.ui.dto.statistics.AttendanceUserCount;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface GetAttendancesByUser {
    public List<AttendanceUserCount> getAttendancesByUsers (LocalDate forDate);
}
