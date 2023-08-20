package com.example.gambarucmsui.ports.interfaces.membership;

import java.time.LocalDate;

public interface IsMembershipPayed {
    public boolean isMembershipPayedByBarcodeAndMonthAndYear(Long barcodeId, LocalDate currentDate);
}
