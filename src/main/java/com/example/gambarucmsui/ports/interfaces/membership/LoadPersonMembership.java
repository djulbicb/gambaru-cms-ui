package com.example.gambarucmsui.ports.interfaces.membership;

import com.example.gambarucmsui.database.entity.PersonMembershipEntity;

import java.time.LocalDate;
import java.util.List;

public interface LoadPersonMembership {
    List<PersonMembershipEntity> getAllMembershipForMonth(LocalDate date);
}
