package com.example.gambarucmsui.ports.impl;

import com.example.gambarucmsui.database.entity.BarcodeEntity;
import com.example.gambarucmsui.database.repo.BarcodeRepository;
import com.example.gambarucmsui.ports.user.BarcodeLoadPort;

import java.util.Optional;

public class BarcodeService implements BarcodeLoadPort {

    private final BarcodeRepository barcodeRepo;

    public BarcodeService(BarcodeRepository barcodeRepo) {
        this.barcodeRepo = barcodeRepo;
    }

    @Override
    public Optional<BarcodeEntity> findById(Long barcodeId) {
        return barcodeRepo.findById(barcodeId);
    }
}
