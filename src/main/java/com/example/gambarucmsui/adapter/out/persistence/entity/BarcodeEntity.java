package com.example.gambarucmsui.adapter.out.persistence.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "barcode")
public class BarcodeEntity {
    public static enum Status {
        NOT_USED, ASSIGNED, DEACTIVATED
    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "barcode_id")
    private Long barcodeId;
    @Enumerated(EnumType.STRING)
    private BarcodeEntity.Status status;

    public Long getBarcodeId() {
        return barcodeId;
    }

    public void setBarcodeId(Long barcodeId) {
        this.barcodeId = barcodeId;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}

