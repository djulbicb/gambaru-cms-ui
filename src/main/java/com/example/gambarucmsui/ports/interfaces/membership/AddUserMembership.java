package com.example.gambarucmsui.ports.interfaces.membership;

import com.example.gambarucmsui.database.entity.BarcodeEntity;
import com.example.gambarucmsui.ports.ValidatorResponse;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface AddUserMembership {
        ValidatorResponse velidateAddMembership(String barcodeId, int month, int year);
        ValidatorResponse validateAndAddMembership(String barcodeId, int month, int year);
        void executeBulkMembership();
        void addToSaveBulkMembership(BarcodeEntity barcode, LocalDateTime dateTime, BigDecimal membershipPayment);
}
