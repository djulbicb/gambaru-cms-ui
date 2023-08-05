package com.example.gambarucmsui.ports.interfaces.membership;

import com.example.gambarucmsui.database.entity.BarcodeEntity;
import com.example.gambarucmsui.database.entity.UserMembershipPaymentEntity;

import java.util.List;

public interface LoadMembership {
    public List<UserMembershipPaymentEntity> findAllMembershipsForMonthAndYear(int month, int year);
    List<UserMembershipPaymentEntity> fetchLastNEntriesForUserMembership(List<BarcodeEntity> barcodeIds, int count);
}
