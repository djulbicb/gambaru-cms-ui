package com.example.gambarucmsui.database.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import org.hibernate.annotations.Index;

@Entity
@Table(name = "person_attendance")
public class PersonAttendanceEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "attendance_id")
    private Long attendanceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "barcode_id")
    private BarcodeEntity barcode;

    @Temporal(TemporalType.TIMESTAMP)
    @Index(name = "idx_attendance_timestamp")
    private LocalDateTime timestamp;

    public PersonAttendanceEntity() {
    }

    public PersonAttendanceEntity(BarcodeEntity barcode, LocalDateTime timestamp) {
        this.barcode = barcode;
        this.timestamp = timestamp;
    }

    public Long getAttendanceId() {
        return attendanceId;
    }

    public void setAttendanceId(Long attendanceId) {
        this.attendanceId = attendanceId;
    }

    public BarcodeEntity getBarcode() {
        return barcode;
    }

    public void setBarcode(BarcodeEntity barcode) {
        this.barcode = barcode;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
