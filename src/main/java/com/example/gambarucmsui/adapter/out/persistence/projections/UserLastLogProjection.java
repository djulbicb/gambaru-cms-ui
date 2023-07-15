package com.example.gambarucmsui.adapter.out.persistence.projections;

import java.time.LocalDateTime;

public interface UserLastLogProjection {
    String getFirstName();
    String getLastName();
    LocalDateTime getLastAttendanceTimestamp();
    LocalDateTime getLastMembershipPaymentTimestamp();
}