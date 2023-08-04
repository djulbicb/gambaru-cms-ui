package com.example.gambarucmsui.ports.user;

import com.example.gambarucmsui.database.entity.BarcodeEntity;

import java.util.Optional;

public interface BarcodeLoadPort {
    Optional<BarcodeEntity> findById(Long barcodeId);
}
