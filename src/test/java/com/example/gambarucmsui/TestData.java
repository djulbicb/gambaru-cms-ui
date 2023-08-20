package com.example.gambarucmsui;

import com.example.gambarucmsui.database.entity.BarcodeEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class TestData {
    public final static LocalDate NOW_DATE = LocalDate.of(2023, 6, 1);
    public final static LocalDateTime NOW_DATE_TIME = LocalDateTime.of(2023, 6, 1, 1, 0, 0); // 21.06.2023

    public final static LocalDate FEBRUARY_START_DATE = LocalDate.of(2023, 2, 1);
    public final static LocalDate FEBRUARY_END_DATE = LocalDate.of(2023, 2, 28);
    public final static LocalDate MARCH_START_DATE = LocalDate.of(2023, 3, 1);

    public static BarcodeEntity dummyBarcode(Long barcodeId) {
        BarcodeEntity barcode = new BarcodeEntity();
        barcode.setBarcodeId(barcodeId);
        barcode.setStatus(BarcodeEntity.Status.NOT_USED);
        return barcode;
    }
}
