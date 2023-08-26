package com.example.gambarucmsui.ports.interfaces.subscription;

import com.example.gambarucmsui.database.entity.SubscriptionEntity;

import java.time.LocalDate;
import java.util.Optional;

public interface AddSubscriptionPort {
    public void addFreeSubscription(Long barcodeId, Long teamId);
    public Optional<SubscriptionEntity> addSubscription(Long barcodeId, Long teamId, LocalDate now, LocalDate end);
}
