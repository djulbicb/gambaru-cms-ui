package com.example.gambarucmsui.ports.interfaces.barcode;

import com.example.gambarucmsui.ports.ValidatorResponse;

public interface BarcodeStatusChangePort {
    ValidatorResponse deactivateBarcode(Long barcodeId);
    ValidatorResponse activateBarcode(Long barcodeId);
}
