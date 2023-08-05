package com.example.gambarucmsui.ports.interfaces.membership;

import com.example.gambarucmsui.database.entity.BarcodeEntity;
import com.example.gambarucmsui.database.entity.PersonMembershipPaymentEntity;

import java.util.List;

public interface LoadMembership {
    public List<PersonMembershipPaymentEntity> findAllMembershipsForMonthAndYear(int month, int year);
    List<PersonMembershipPaymentEntity> fetchLastNEntriesForUserMembership(List<BarcodeEntity> barcodeIds, int count);
}
