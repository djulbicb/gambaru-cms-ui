package com.example.gambarucmsui.ports.interfaces.subscription;

import java.time.LocalDate;

public interface UpdateSubscriptionPort {
    void updateSubsscription(Long barcodeId, boolean isFreeOfCharge, LocalDate startDate, LocalDate endDate);
}
