package com.example.gambarucmsui.adapter.out.persistence.entity.user;

import com.example.gambarucmsui.adapter.out.persistence.entity.BarcodeEntity;
import com.example.gambarucmsui.adapter.out.persistence.entity.TeamEntity;
import jakarta.persistence.*;

import java.time.LocalDateTime;


@Entity
@Table(name = "user")
public class UserEntity {
    public static enum Gender {
        MALE, FEMALE
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "first_name", columnDefinition = "VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_general_ci")
    private String firstName;

    @Column(name = "last_name", columnDefinition = "VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_general_ci")
    private String lastName;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(name = "team_name")
    private String teamName;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "barcode_id")
    private BarcodeEntity barcode;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "team_id")
    private TeamEntity team;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "last_attendance_timestamp")
    private LocalDateTime lastAttendanceTimestamp;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "last_membership_payment_timestamp")
    private LocalDateTime lastMembershipPaymentTimestamp;

    @Override
    public String toString() {
        return "UserEntity{" +
                "userId=" + userId +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", gender=" + gender +
                ", teamName='" + teamName + '\'' +
                ", barcode=" + barcode +
                ", team=" + team +
                ", createdAt=" + createdAt +
                ", lastAttendanceTimestamp=" + lastAttendanceTimestamp +
                ", lastMembershipPaymentTimestamp=" + lastMembershipPaymentTimestamp +
                '}';
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public BarcodeEntity getBarcode() {
        return barcode;
    }

    public void setBarcode(BarcodeEntity barcode) {
        this.barcode = barcode;
    }

    public TeamEntity getTeam() {
        return team;
    }

    public void setTeam(TeamEntity team) {
        this.team = team;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
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
}
