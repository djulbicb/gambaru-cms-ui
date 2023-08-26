package com.example.gambarucmsui.ports.interfaces.subscription;

import com.example.gambarucmsui.database.entity.BarcodeEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface AddSubscriptionPort {
    public void addFreeSubscription(Long barcodeId, Long teamId);
    public void addSubscription(Long barcodeId, Long teamId, LocalDate now, LocalDate end);
}
