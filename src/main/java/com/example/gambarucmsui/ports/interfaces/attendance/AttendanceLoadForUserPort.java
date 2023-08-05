package com.example.gambarucmsui.ports.interfaces.attendance;

import com.example.gambarucmsui.database.entity.BarcodeEntity;
import com.example.gambarucmsui.database.entity.PersonAttendanceEntity;

import java.time.LocalDate;
import java.util.List;

public interface AttendanceLoadForUserPort {
    List<PersonAttendanceEntity> findAllForAttendanceDate(LocalDate forDate);
    List<PersonAttendanceEntity> fetchLastNEntriesForUserAttendance(List<BarcodeEntity> barcodeIds, int count);
}
