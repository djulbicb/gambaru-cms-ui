package com.example.gambarucmsui.ports.impl;

import com.example.gambarucmsui.database.entity.*;
import com.example.gambarucmsui.database.repo.BarcodeRepository;
import com.example.gambarucmsui.database.repo.UserAttendanceRepository;
import com.example.gambarucmsui.database.repo.UserMembershipRepository;
import com.example.gambarucmsui.ports.ValidatorResponse;
import com.example.gambarucmsui.ports.interfaces.membership.AddUserMembership;
import com.example.gambarucmsui.ports.interfaces.membership.IsMembershipPayed;
import com.example.gambarucmsui.ports.interfaces.membership.LoadMembership;
import com.example.gambarucmsui.ports.interfaces.membership.MembershipPurgePort;
import com.example.gambarucmsui.ui.ToastView;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static com.example.gambarucmsui.database.entity.BarcodeEntity.BARCODE_ID;
import static com.example.gambarucmsui.util.FormatUtil.isLong;
import static com.example.gambarucmsui.util.FormatUtil.parseBarcodeStr;

public class MembershipService implements AddUserMembership, LoadMembership, IsMembershipPayed, MembershipPurgePort {

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
    public ValidatorResponse velidateAddMembership(String barcodeIdStr, int month, int year) {
        Map<String, String> errors = new HashMap<>();
        if (!isLong(barcodeIdStr)) {
            if (barcodeIdStr.isBlank()) {
                errors.put(BARCODE_ID, "");
            } else {
                errors.put(BARCODE_ID, "Upiši barkod npr 123.");
            }
            return new ValidatorResponse(errors);
        }
        Long barcodeId = parseBarcodeStr(barcodeIdStr);
        Optional<BarcodeEntity> barcodeEntityOptional = barcodeRepo.findById(barcodeId);
        if (barcodeEntityOptional.isEmpty()) {
            errors.put(BARCODE_ID, "Taj barkod uopšte nije registrovan u bazi.");
            return new ValidatorResponse(errors);
        }
        BarcodeEntity b = barcodeEntityOptional.get();
        if (b.getStatus() == BarcodeEntity.Status.DELETED)  {
            errors.put(BARCODE_ID, "Taj barkod je obrisan. Tim sa kojim je asociran je obrisan.");
            return new ValidatorResponse(errors);
        }
        if (b.getStatus() == BarcodeEntity.Status.NOT_USED)  {
            errors.put(BARCODE_ID, "Taj barkod nije zadat ijednom korisniku.");
            return new ValidatorResponse(errors);
        }
        if (membershipRepo.isMembershipPayedByBarcodeAndMonthAndYear(barcodeId, month, year)) {
            errors.put(BARCODE_ID, "Članarina za ovaj mesec je već plaćena.");
            return new ValidatorResponse(errors);
        }
        return new ValidatorResponse(errors);
    }

    @Override
    public ValidatorResponse validateAndAddMembership(String barcodeId, int month, int year) {
        ValidatorResponse res = velidateAddMembership(barcodeId, month, year);
        if (res.isOk()) {
            BarcodeEntity barcode = barcodeRepo.findById(parseBarcodeStr(barcodeId)).get();
            BigDecimal membershipPayment = barcode.getTeam().getMembershipPayment();
            LocalDateTime now = LocalDateTime.now();
            PersonMembershipPaymentEntity entity = new PersonMembershipPaymentEntity(barcode, month, year, now, membershipPayment);
            barcode.setLastMembershipPaymentTimestamp(now);

            membershipRepo.save(entity);

            PersonEntity person = barcode.getPerson();

            return new ValidatorResponse(String.format("Članarina plaćena za %s %s.", person.getFirstName(), person.getLastName()));
        }
        return res;
    }

    List<PersonMembershipPaymentEntity> membershipPaymentEntities = new ArrayList<>();
    @Override
    public void addToSaveBulkMembership(BarcodeEntity barcode,LocalDateTime timestamp, BigDecimal membershipPayment) {
        membershipPaymentEntities.add(new PersonMembershipPaymentEntity(barcode, timestamp.getMonthValue(), timestamp.getYear(), LocalDateTime.now(), membershipPayment));
    }

    @Override
    public void executeBulkMembership() {
        membershipRepo.saveAll(membershipPaymentEntities);
        membershipPaymentEntities.clear();
    }


    @Override
    public List<PersonMembershipPaymentEntity> findAllMembershipsForMonthAndYear(int month, int year) {
        return membershipRepo.findAllMembershipsForMonthAndYear(month, year);
    }

    @Override
    public List<PersonMembershipPaymentEntity> fetchLastNEntriesForUserMembership(List<BarcodeEntity> barcodeIds, int count) {
        return membershipRepo.fetchLastNEntriesForUserMembership(barcodeIds, count);
    }

    @Override
    public boolean isMembershipPayedByBarcodeAndMonthAndYear(Long barcodeId, int month, int year) {
        return membershipRepo.isMembershipPayedByBarcodeAndMonthAndYear(barcodeId, month, year);
    }

    @Override
    public void purge() {
        membershipRepo.deleteAll();
    }
}
