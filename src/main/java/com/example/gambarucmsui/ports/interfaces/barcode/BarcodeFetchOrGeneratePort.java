package com.example.gambarucmsui.ports.interfaces.barcode;

import com.example.gambarucmsui.database.entity.BarcodeEntity;

import java.util.List;

public interface BarcodeFetchOrGeneratePort {
    List<BarcodeEntity> fetchOrGenerateBarcodes(int count, BarcodeEntity.Status status);
    List<BarcodeEntity> generateNewBarcodes(int generateCount);
    BarcodeEntity fetchOneOrGenerate(BarcodeEntity.Status status);
}
