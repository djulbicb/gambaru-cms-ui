package com.example.gambarucmsui.ports.interfaces.attendance;

import com.example.gambarucmsui.database.entity.BarcodeEntity;
import com.example.gambarucmsui.ports.ValidatorResponse;

import java.time.LocalDateTime;

public interface AttendanceAddForUserPort {
    public ValidatorResponse verifyAddAttendance(String barcodeId);
    public ValidatorResponse verifyAndAddAttendance(Long barcodeId, LocalDateTime timestamp);

    void addToSaveBulk(BarcodeEntity barcode, LocalDateTime dateTime);

    void executeBulk();
}
