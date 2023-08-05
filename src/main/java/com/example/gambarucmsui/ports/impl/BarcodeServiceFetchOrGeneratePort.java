package com.example.gambarucmsui.ports.impl;

import com.example.gambarucmsui.database.entity.BarcodeEntity;
import com.example.gambarucmsui.database.repo.BarcodeRepository;
import com.example.gambarucmsui.ports.interfaces.barcode.BarcodeLoadPort;
import com.example.gambarucmsui.ports.interfaces.barcode.BarcodeUpdatePort;
import com.example.gambarucmsui.ports.interfaces.barcode.BarcodeFetchOrGeneratePort;

import java.util.List;
import java.util.Optional;

public class BarcodeServiceFetchOrGeneratePort implements BarcodeLoadPort, BarcodeFetchOrGeneratePort, BarcodeUpdatePort {

    private final BarcodeRepository barcodeRepo;

    public BarcodeServiceFetchOrGeneratePort(BarcodeRepository barcodeRepo) {
        this.barcodeRepo = barcodeRepo;
    }

    @Override
    public Optional<BarcodeEntity> findById(Long barcodeId) {
        return barcodeRepo.findById(barcodeId);
    }

    @Override
    public List<BarcodeEntity> findByIds(List<Long> collect) {
        return barcodeRepo.findByIds("barcodeId", collect);
    }

    @Override
    public List<BarcodeEntity> findAllByStatus(BarcodeEntity.Status status) {
        return barcodeRepo.findAllByStatus(status);
    }

    @Override
    public List<BarcodeEntity> findByTeam(Long teamId) {
        return barcodeRepo.findByTeam(teamId);
    }

    @Override
    public List<BarcodeEntity> fetchOrGenerateBarcodes(int count, BarcodeEntity.Status status) {
        return barcodeRepo.fetchOrGenerateBarcodes(count, status);
    }

    @Override
    public List<BarcodeEntity> generateNewBarcodes(int generateCount) {
        return barcodeRepo.generateNewBarcodes(generateCount);
    }

    @Override
    public BarcodeEntity fetchOneOrGenerate(BarcodeEntity.Status status) {
        return barcodeRepo.fetchOneOrGenerate(status);
    }

    @Override
    public Optional<BarcodeEntity> updateBarcode(Long barcodeId, BarcodeEntity.Status status) {
        Optional<BarcodeEntity> byId = findById(barcodeId);

        if (byId.isEmpty()) {
            return Optional.empty();
        }
        BarcodeEntity barcodeEntity = byId.get();

        if (barcodeEntity.getStatus() == BarcodeEntity.Status.DELETED) {
            return Optional.empty();
        }

        barcodeEntity.setStatus(status);
        barcodeRepo.update(barcodeEntity);
        return Optional.of(barcodeEntity);
    }
}
