package com.example.gambarucmsui.ports.interfaces.membership;

import com.example.gambarucmsui.database.entity.BarcodeEntity;
import com.example.gambarucmsui.ports.ValidatorResponse;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public interface AddUserMembership {
        ValidatorResponse velidateAddMembership(String barcodeId, LocalDate currentDate);
        ValidatorResponse validateAndAddMembership(String barcodeId, LocalDate currentDate);
        void executeBulkMembership();
        void addToSaveBulkMembership(BarcodeEntity barcode, LocalDateTime dateTime, BigDecimal membershipPayment);
}
