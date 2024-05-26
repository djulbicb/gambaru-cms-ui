package com.example.gambarucmsui.ports.interfaces.barcode;

import com.example.gambarucmsui.database.entity.BarcodeEntity;

import java.util.Optional;

public interface BarcodeUpdatePort {
    public Optional<BarcodeEntity> updateBarcode(Long barcodeId, BarcodeEntity.Status status);

    Optional<BarcodeEntity> updateDiscount(Long barcodeId, int discount);
}
