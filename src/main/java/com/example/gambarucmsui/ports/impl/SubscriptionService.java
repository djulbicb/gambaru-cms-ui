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

import static com.example.gambarucmsui.common.Props.FUTURE;
import static com.example.gambarucmsui.common.Props.PAST;

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

    public void addFreeSubscription(Long barcodeId, Long teamId) {
        Optional<BarcodeEntity> byId = barcodeRepo.findById(barcodeId);
        if (byId.isPresent()) {
            BarcodeEntity barcode = byId.get();

            SubscriptionEntity en = new SubscriptionEntity();
            en.setFreeOfCharge(true);
            en.setStartDate(PAST);
            en.setEndDate(FUTURE);
            en.setBarcode(barcode);

            subscriptionRepo.save(en);

            barcode.setSubscription(en);
            barcodeRepo.update(barcode);
        }
    }

    public void addSubscription(Long barcodeId, Long teamId, LocalDate now, LocalDate end) {
        Optional<BarcodeEntity> byId = barcodeRepo.findById(barcodeId);
        if (byId.isPresent()) {
            BarcodeEntity barcode = byId.get();

            SubscriptionEntity en = new SubscriptionEntity();
            en.setFreeOfCharge(false);
            en.setStartDate(now);
            en.setEndDate(end);
            en.setBarcode(barcode);

            subscriptionRepo.save(en);

            barcode.setSubscription(en);
            barcodeRepo.update(barcode);
        }
    }
}
//
//    @Override
//    public void addFreeSubscription(Long barcodeId, String teamName) {
//        Optional<BarcodeEntity> byId = barcodeRepo.findById(barcodeId);
//        if (byId.isPresent()) {
//            BarcodeEntity barcode = byId.get();
//            SubscriptionEntity en = new SubscriptionEntity(barcode, true, LocalDate.MIN, LocalDate.MAX);
//            subscriptionRepo.save(en);
//            barcode.setSubscription(en);
//            barcodeRepo.update(barcode);
//        }
//
//    }
//
//    @Override
//    public void addSubscription(Long barcodeId, String teamName, LocalDate start, LocalDate end) {
//        Optional<BarcodeEntity> byId = barcodeRepo.findById(barcodeId);
//        if (byId.isPresent()) {
//            BarcodeEntity barcode = byId.get();
//
//
//            SubscriptionEntity en = new SubscriptionEntity(barcode, false, start, end);
//            subscriptionRepo.save(en);
//            barcode.setSubscription(en);
//
//            barcodeRepo.update(barcode);
//        }
//    }