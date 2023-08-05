package com.example.gambarucmsui.database.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "barcode")
public class BarcodeEntity {

    public static enum Status {
        NOT_USED, ASSIGNED, DEACTIVATED, DELETED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "barcode_id")
    private Long barcodeId;
    public static final String BARCODE_ID = "barcodeId";

    @Enumerated(EnumType.STRING)
    private Status status;

    @ManyToOne
    @JoinColumn(name = "person_id")
    private PersonEntity person;

    @ManyToOne
    @JoinColumn(name = "team_id")
    private TeamEntity team;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "assigned_timestamp")
    private LocalDateTime assignedTimestamp;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "last_attendance_timestamp")
    private LocalDateTime lastAttendanceTimestamp;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "last_membership_payment_timestamp")
    private LocalDateTime lastMembershipPaymentTimestamp;



    // Constructors, getters, setters, and other fields/methods ...

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

    public PersonEntity getPerson() {
        return person;
    }

    public void setPerson(PersonEntity person) {
        this.person = person;
    }

    public TeamEntity getTeam() {
        return team;
    }

    public void setTeam(TeamEntity team) {
        this.team = team;
    }

    public boolean isAssigned() {
        return status.equals(Status.ASSIGNED);
    }

    public LocalDateTime getLastAttendanceTimestamp() {
        return lastAttendanceTimestamp;
    }

    public void setLastAttendanceTimestamp(LocalDateTime lastAttendanceTimestamp) {
        this.lastAttendanceTimestamp = lastAttendanceTimestamp;
    }

    public LocalDateTime getLastMembershipPaymentTimestamp() {
        return lastMembershipPaymentTimestamp;
    }

    public void setLastMembershipPaymentTimestamp(LocalDateTime lastMembershipPaymentTimestamp) {
        this.lastMembershipPaymentTimestamp = lastMembershipPaymentTimestamp;
    }

    public LocalDateTime getAssignedTimestamp() {
        return assignedTimestamp;
    }

    public void setAssignedTimestamp(LocalDateTime assignedTimestamp) {
        this.assignedTimestamp = assignedTimestamp;
    }

    @Override
    public String toString() {
        return "BarcodeEntity{" +
                "barcodeId=" + barcodeId +
                ", status=" + status +
                ", person=" + person +
                ", team=" + team +
                ", assignedTimestamp=" + assignedTimestamp +
                ", lastAttendanceTimestamp=" + lastAttendanceTimestamp +
                ", lastMembershipPaymentTimestamp=" + lastMembershipPaymentTimestamp +
                '}';
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        BarcodeEntity barcode = (BarcodeEntity) object;
        return Objects.equals(barcodeId, barcode.barcodeId) && status == barcode.status && Objects.equals(person, barcode.person) && Objects.equals(team, barcode.team) && Objects.equals(assignedTimestamp, barcode.assignedTimestamp) && Objects.equals(lastAttendanceTimestamp, barcode.lastAttendanceTimestamp) && Objects.equals(lastMembershipPaymentTimestamp, barcode.lastMembershipPaymentTimestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(barcodeId, status, person, team, assignedTimestamp, lastAttendanceTimestamp, lastMembershipPaymentTimestamp);
    }
}


