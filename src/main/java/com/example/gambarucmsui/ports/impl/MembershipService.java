package com.example.gambarucmsui.ports.impl;

import com.example.gambarucmsui.database.entity.*;
import com.example.gambarucmsui.database.repo.BarcodeRepository;
import com.example.gambarucmsui.database.repo.UserAttendanceRepository;
import com.example.gambarucmsui.database.repo.UserMembershipRepository;
import com.example.gambarucmsui.ports.interfaces.membership.AddUserMembership;
import com.example.gambarucmsui.ports.interfaces.membership.IsMembershipPayed;
import com.example.gambarucmsui.ports.interfaces.membership.LoadMembership;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

public class MembershipService implements AddUserMembership, LoadMembership, IsMembershipPayed {

    private final UserAttendanceRepository attendanceRepo;
    private final UserMembershipRepository membershipRepo;
    private final BarcodeRepository barcodeRepo;

    public MembershipService(
            BarcodeRepository barcodeRepo,
            UserAttendanceRepository attendanceRepo,
            UserMembershipRepository membershipRepo) {
        this.barcodeRepo = barcodeRepo;
        this.attendanceRepo = attendanceRepo;
        this.membershipRepo = membershipRepo;
    }

    @Override
    public void addMembership(Long barcodeId, int month, int year, BigDecimal membershipPayment) {
        Optional<BarcodeEntity> byId = barcodeRepo.findById(barcodeId);
        if (byId.isPresent()) {
            BarcodeEntity barcode = byId.get();

            LocalDateTime now = LocalDateTime.now();
            UserMembershipPaymentEntity entity = new UserMembershipPaymentEntity(barcode, month, year, now, membershipPayment);
            barcode.setLastMembershipPaymentTimestamp(now);

            membershipRepo.save(entity);
        }
    }

    List<UserMembershipPaymentEntity> membershipPaymentEntities = new ArrayList<>();
    @Override
    public void addToSaveBulkMembership(BarcodeEntity barcode,LocalDateTime timestamp, BigDecimal membershipPayment) {
        membershipPaymentEntities.add(new UserMembershipPaymentEntity(barcode, timestamp.getMonthValue(), timestamp.getYear(), LocalDateTime.now(), membershipPayment));
    }

    @Override
    public void executeBulkMembership() {
        membershipRepo.saveAll(membershipPaymentEntities);
        membershipPaymentEntities.clear();
    }


    @Override
    public List<UserMembershipPaymentEntity> findAllMembershipsForMonthAndYear(int month, int year) {
        return membershipRepo.findAllMembershipsForMonthAndYear(month, year);
    }

    @Override
    public List<UserMembershipPaymentEntity> fetchLastNEntriesForUserMembership(List<BarcodeEntity> barcodeIds, int count) {
        return membershipRepo.fetchLastNEntriesForUserMembership(barcodeIds, count);
    }

    @Override
    public boolean isMembershipPayedByBarcodeAndMonthAndYear(Long barcodeId, int month, int year) {
        return membershipRepo.isMembershipPayedByBarcodeAndMonthAndYear(barcodeId, month, year);
    }
}