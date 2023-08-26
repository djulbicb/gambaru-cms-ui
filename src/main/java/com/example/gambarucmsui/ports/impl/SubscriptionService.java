package com.example.gambarucmsui.ports.impl;

import com.example.gambarucmsui.database.entity.BarcodeEntity;
import com.example.gambarucmsui.database.entity.SubscriptionEntity;
import com.example.gambarucmsui.database.repo.BarcodeRepository;
import com.example.gambarucmsui.database.repo.SubscriptionRepository;
import com.example.gambarucmsui.ports.interfaces.subscription.AddSubscriptionPort;
import com.example.gambarucmsui.ports.interfaces.subscription.SubscriptionLoadPort;
import com.example.gambarucmsui.ports.interfaces.subscription.SubscriptionPurgePort;
import com.example.gambarucmsui.ports.interfaces.subscription.UpdateSubscriptionPort;

import java.time.LocalDate;
import java.util.Optional;

public class SubscriptionService implements AddSubscriptionPort, UpdateSubscriptionPort, SubscriptionLoadPort, SubscriptionPurgePort {

    private final SubscriptionRepository subscriptionRepo;
    private final BarcodeRepository barcodeRepo;

    public SubscriptionService(SubscriptionRepository subscriptionRepo, BarcodeRepository barcodeRepo) {
        this.subscriptionRepo = subscriptionRepo;
        this.barcodeRepo = barcodeRepo;
    }

    @Override
    public void purge() {
        subscriptionRepo.deleteAll();
    }

    @Override
    public Optional<SubscriptionEntity> findByBarcodeId(Long barcodeId) {
        return subscriptionRepo.findByBarcodeId(barcodeId);
    }



    @Override
    public void updateSubsscription(Long barcodeId, boolean isFreeOfCharge, LocalDate startDate, LocalDate endDate) {
        Optional<BarcodeEntity> barcode = barcodeRepo.findById(barcodeId);
        if (barcode.isPresent()) {
            Optional<SubscriptionEntity> subOpt = subscriptionRepo.findByBarcodeId(barcodeId);
            if (subOpt.isEmpty()) {
                return;
            }

            SubscriptionEntity subscription = subOpt.get();
            if (isFreeOfCharge) {
                subscription.setFreeOfCharge(true);
                subscription.setStartDate(LocalDate.MIN);
                subscription.setEndDate(LocalDate.MAX);
                subscriptionRepo.update(subscription);
                return;
            }

            subscription.setFreeOfCharge(false);
            subscription.setStartDate(startDate);
            subscription.setEndDate(endDate);
            subscriptionRepo.update(subscription);
        }
    }

    @Override
    public void addSubscription(Long barcodeId, LocalDate currentDay, boolean isFreeOfCharge, boolean isPayNextMonth) {
        Optional<BarcodeEntity> byId = barcodeRepo.findById(barcodeId);
        if (byId.isPresent()) {
            BarcodeEntity barcode = byId.get();

            if (isFreeOfCharge) {
                SubscriptionEntity en = new SubscriptionEntity(barcode, true, LocalDate.MIN, LocalDate.MAX);
                subscriptionRepo.save(en);
                barcode.setSubscription(en);
            } else if (isPayNextMonth) {
                SubscriptionEntity en = new SubscriptionEntity(barcode, false, currentDay, currentDay.plusMonths(1));
                subscriptionRepo.save(en);
                barcode.setSubscription(en);
            } else {
                SubscriptionEntity en = new SubscriptionEntity(barcode, false, null, null);
                subscriptionRepo.save(en);
                barcode.setSubscription(en);
            }

            barcodeRepo.update(barcode);
        }
    }
}
