package com.example.gambarucmsui.database.entity;

import java.time.LocalDate;
import java.util.List;

public interface LoadAttendanceForUser {
    List<UserAttendanceEntity> findAllForAttendanceDate(LocalDate forDate);
}
