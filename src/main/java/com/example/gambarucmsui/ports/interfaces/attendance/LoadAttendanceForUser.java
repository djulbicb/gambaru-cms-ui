package com.example.gambarucmsui.ports.interfaces.attendance;

import com.example.gambarucmsui.database.entity.BarcodeEntity;
import com.example.gambarucmsui.database.entity.UserAttendanceEntity;

import java.time.LocalDate;
import java.util.List;

public interface LoadAttendanceForUser {
    List<UserAttendanceEntity> findAllForAttendanceDate(LocalDate forDate);
    List<UserAttendanceEntity> fetchLastNEntriesForUserAttendance(List<BarcodeEntity> barcodeIds, int count);
}
