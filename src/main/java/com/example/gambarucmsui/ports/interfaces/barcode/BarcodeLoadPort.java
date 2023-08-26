package com.example.gambarucmsui.ports.interfaces.barcode;

import com.example.gambarucmsui.database.entity.BarcodeEntity;
import com.example.gambarucmsui.ports.ValidatorResponse;

import java.util.List;
import java.util.Optional;

public interface BarcodeLoadPort {
    ValidatorResponse validateBarcodeActive(String barcodeId);
    Optional<BarcodeEntity> findById(Long barcodeId);

    List<BarcodeEntity> findByIds(List<Long> collect); // "barcodeId"

    List<BarcodeEntity> findAllByStatus(BarcodeEntity.Status status);

    List<BarcodeEntity> findByTeam(Long teamId);
}
