package com.example.gambarucmsui.ports.impl;

import com.example.gambarucmsui.database.entity.*;
import com.example.gambarucmsui.database.repo.BarcodeRepository;
import com.example.gambarucmsui.database.repo.UserAttendanceRepository;
import com.example.gambarucmsui.database.repo.UserMembershipRepository;
import com.example.gambarucmsui.ports.ValidatorResponse;
import com.example.gambarucmsui.ports.user.AddUserAttendancePort;
import com.example.gambarucmsui.ports.user.AddUserMembership;
import com.example.gambarucmsui.ui.ToastView;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static com.example.gambarucmsui.util.FormatUtil.isLong;
import static com.example.gambarucmsui.util.FormatUtil.parseBarcodeStr;

public class AttendanceAndMembershipServicePort implements AddUserAttendancePort, AddUserMembership, LoadAttendanceForUser {

    private final UserAttendanceRepository attendanceRepo;
    private final UserMembershipRepository membershipRepo;
    private final BarcodeRepository barcodeRepo;

    public AttendanceAndMembershipServicePort(
            BarcodeRepository barcodeRepo,
            UserAttendanceRepository attendanceRepo,
            UserMembershipRepository membershipRepo) {
        this.barcodeRepo = barcodeRepo;
        this.attendanceRepo = attendanceRepo;
        this.membershipRepo = membershipRepo;
    }

    @Override
    public ValidatorResponse verifyForAttendance(String barcodeIdStr) {
        Map<String, String> errors = new HashMap<>();
        if (!isLong(barcodeIdStr)) {
            if (barcodeIdStr.isBlank()) {
                errors.put("barcodeId", "");
            } else {
                errors.put("barcodeId", "Upiši barkod npr 123.");
            }
            return new ValidatorResponse(errors);
        }
        Long barcode = parseBarcodeStr(barcodeIdStr);
        Optional<BarcodeEntity> barcodeEntityOptional = barcodeRepo.findById(barcode);
        if (barcodeEntityOptional.isEmpty()) {
            errors.put("barcodeId", "Taj barkod nije registrovan u bazi.");
            return new ValidatorResponse(errors);
        }

        BarcodeEntity b = barcodeEntityOptional.get();
        if (b.getStatus() != BarcodeEntity.Status.ASSIGNED)  {
            errors.put("barcodeId", "Taj barkod postoji ali nije u upotrebi. Probaj drugi drugi.");
            return new ValidatorResponse(errors);
        }
        return new ValidatorResponse(errors);
    }

    @Override
    public void addAttendance(Long barcodeId, LocalDateTime timestamp) {
        Optional<BarcodeEntity> optBarcode = barcodeRepo.findById(barcodeId);
        if (optBarcode.isPresent()) {
            BarcodeEntity barcode = optBarcode.get();

            if (barcode.getStatus() != BarcodeEntity.Status.ASSIGNED) {
                ToastView.showModal("Barkod se trenutno ne koristi.");
                return;
            }

            attendanceRepo.save(new UserAttendanceEntity(barcode, timestamp));

            UserEntity user = barcode.getUser();
            ToastView.showAttendance(user);
        } else {
            ToastView.showModal("Barkod ne postoji u sistemu.");
        }
    }

    List<UserAttendanceEntity> attendanceEntityList = new ArrayList<>();
    @Override
    public void addToSaveBulk(BarcodeEntity barcode, LocalDateTime timestamp) {
        attendanceEntityList.add(new UserAttendanceEntity(barcode, timestamp));
    }
    @Override
    public void executeBulk() {
        attendanceRepo.saveAll(attendanceEntityList);
        attendanceEntityList.clear();
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
    public List<UserAttendanceEntity> findAllForAttendanceDate(LocalDate forDate) {
        return attendanceRepo.findAllForAttendanceDate(forDate);
    }
}
