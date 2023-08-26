package com.example.gambarucmsui.ports.interfaces.subscription;

import com.example.gambarucmsui.database.entity.BarcodeEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface AddSubscriptionPort {
    void addSubscription(Long barcodeId, LocalDate currentDay, boolean isFreeOfCharge, boolean isPayNextMonth);
}
