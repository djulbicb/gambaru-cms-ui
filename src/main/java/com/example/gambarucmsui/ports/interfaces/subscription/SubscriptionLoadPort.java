package com.example.gambarucmsui.ports.interfaces.subscription;

import com.example.gambarucmsui.database.entity.SubscriptionEntity;

import java.util.Optional;

public interface SubscriptionLoadPort {
    Optional<SubscriptionEntity> findByBarcodeId(Long userId);
}
