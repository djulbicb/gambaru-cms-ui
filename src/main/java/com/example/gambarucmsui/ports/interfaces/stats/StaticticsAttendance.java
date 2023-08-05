package com.example.gambarucmsui.ports.interfaces.stats;

import com.example.gambarucmsui.ui.dto.statistics.AttendanceCount;

import java.util.List;

public interface StaticticsAttendance {
    public List<AttendanceCount> getAttendanceDataLast60Days();
}
