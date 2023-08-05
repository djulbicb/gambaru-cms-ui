package com.example.gambarucmsui.ports.interfaces.membership;

public interface IsMembershipPayed {
    public boolean isMembershipPayedByBarcodeAndMonthAndYear(Long barcodeId, int month, int year);
}
