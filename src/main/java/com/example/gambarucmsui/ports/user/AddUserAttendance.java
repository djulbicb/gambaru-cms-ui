package com.example.gambarucmsui.ports.user;

import com.example.gambarucmsui.database.entity.BarcodeEntity;

import java.time.LocalDateTime;

public interface AddUserAttendance {
    public void addAttendance(Long barcodeId, LocalDateTime timestamp);

    void addToSaveBulk(BarcodeEntity barcode, LocalDateTime dateTime);

    void executeBulk();
}
