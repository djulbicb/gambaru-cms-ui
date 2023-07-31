package com.example.gambarucmsui.ui.dto.admin.subtables;

import com.example.gambarucmsui.database.entity.BarcodeEntity;

public class BarcodeDetail {
    private final String barcode;
    private final String team;
    private final String status;

    public BarcodeDetail(String barcode, BarcodeEntity.Status status, String team) {
        this.barcode = barcode;
        this.team = team;

        if (status == BarcodeEntity.Status.ASSIGNED) {
            this.status = "Aktivan";
        } else if (status == BarcodeEntity.Status.DEACTIVATED) {
            this.status = "Deaktivan";
        } else {
            this.status = "Neaktivan";
        }
    }

    public String getBarcode() {
        return barcode;
    }

    public String getTeam() {
        return team;
    }

    public String getStatus() {
        return status;
    }
}
