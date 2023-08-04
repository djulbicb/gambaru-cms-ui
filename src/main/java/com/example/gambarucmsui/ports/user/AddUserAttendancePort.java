package com.example.gambarucmsui.ports.user;

import com.example.gambarucmsui.database.entity.BarcodeEntity;
import com.example.gambarucmsui.ports.ValidatorResponse;

import java.time.LocalDateTime;

public interface AddUserAttendancePort {
    public ValidatorResponse verifyForAttendance(String barcodeId);
    public void addAttendance(Long barcodeId, LocalDateTime timestamp);

    void addToSaveBulk(BarcodeEntity barcode, LocalDateTime dateTime);

    void executeBulk();
}
