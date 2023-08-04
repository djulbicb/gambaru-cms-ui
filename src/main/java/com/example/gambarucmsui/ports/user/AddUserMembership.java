package com.example.gambarucmsui.ports.user;

import com.example.gambarucmsui.database.entity.BarcodeEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface AddUserMembership {
        void addMembership(Long barcodeId, int month, int year, BigDecimal membershipPayment);

        void executeBulkMembership();

        void addToSaveBulkMembership(BarcodeEntity barcode, LocalDateTime dateTime, BigDecimal membershipPayment);
}
