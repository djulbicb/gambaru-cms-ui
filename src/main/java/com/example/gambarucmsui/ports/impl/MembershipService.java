package com.example.gambarucmsui.ports.impl;

import com.example.gambarucmsui.database.entity.*;
import com.example.gambarucmsui.database.repo.BarcodeRepository;
import com.example.gambarucmsui.database.repo.UserAttendanceRepository;
import com.example.gambarucmsui.database.repo.UserMembershipRepository;
import com.example.gambarucmsui.ports.ValidatorResponse;
import com.example.gambarucmsui.ports.interfaces.membership.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static com.example.gambarucmsui.common.Messages.*;
import static com.example.gambarucmsui.database.entity.BarcodeEntity.BARCODE_ID;
import static com.example.gambarucmsui.util.FormatUtil.isLong;
import static com.example.gambarucmsui.util.FormatUtil.parseBarcodeStr;

public class MembershipService implements AddUserMembership, LoadMembership, IsMembershipPayed, MembershipPurgePort, GetMembershipStatusPort {

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
    public ValidatorResponse velidateAddMembership(String barcodeIdStr, LocalDateTime currentDate) {
        Map<String, String> errors = new HashMap<>();
        if (!isLong(barcodeIdStr)) {
            if (barcodeIdStr.isBlank()) {
                errors.put(BARCODE_ID, "");
            } else {
                errors.put(BARCODE_ID, BARCODE_WRONG_FORMAT);
            }
            return new ValidatorResponse(errors);
        }
        Long barcodeId = parseBarcodeStr(barcodeIdStr);
        Optional<BarcodeEntity> barcodeEntityOptional = barcodeRepo.findById(barcodeId);
        if (barcodeEntityOptional.isEmpty()) {
            errors.put(BARCODE_ID, BARCODE_NOT_REGISTERED);
            return new ValidatorResponse(errors);
        }
        BarcodeEntity b = barcodeEntityOptional.get();
        if (b.getStatus() == BarcodeEntity.Status.DELETED)  {
            errors.put(BARCODE_ID, "Taj barkod je obrisan. Tim sa kojim je asociran je obrisan.");
            return new ValidatorResponse(errors);
        }
        if (b.getStatus() == BarcodeEntity.Status.NOT_USED)  {
            errors.put(BARCODE_ID, BARCODE_IS_NOT_ASSIGNED);
            return new ValidatorResponse(errors);
        }
        if (barcodeRepo.isMembershipPayedByBarcodeAndMonthAndYear(barcodeId, currentDate.toLocalDate())) {
            errors.put(BARCODE_ID, MEMBERSHIP_ALREADY_PAYED);
            return new ValidatorResponse(errors);
        }
        return new ValidatorResponse(errors);
    }

    @Override
    public ValidatorResponse validateAndAddMembership(String barcodeId, LocalDateTime currendDate) {
        ValidatorResponse res = velidateAddMembership(barcodeId, currendDate);
        if (res.isOk()) {
            BarcodeEntity barcode = barcodeRepo.findById(parseBarcodeStr(barcodeId)).get();
            BigDecimal membershipPayment = barcode.getTeam().getMembershipPayment();

            int year = currendDate.getYear();
            int month = currendDate.getMonthValue();

            PersonMembershipPaymentEntity entity = new PersonMembershipPaymentEntity(barcode, month, year, currendDate, membershipPayment);
            barcode.setLastMembershipPaymentTimestamp(currendDate);

            membershipRepo.save(entity);

            PersonEntity person = barcode.getPerson();

            return new ValidatorResponse(MEMBERSHIP_PAYMENT_ADDED(person.getFirstName(), person.getLastName()));
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
    public boolean isMembershipPayedByBarcodeAndMonthAndYear(Long barcodeId, LocalDate currentDate) {
        return barcodeRepo.isMembershipPayedByBarcodeAndMonthAndYear(barcodeId, currentDate);
    }

    @Override
    public State getLastMembershipForUser(LocalDateTime payment, LocalDateTime currentDate) {
        if (payment == null || currentDate == null) {
            return State.empty();
        }
        
        long daysBetween = ChronoUnit.DAYS.between(payment, currentDate);

        YearMonth yearMonth = YearMonth.of(payment.getYear(), payment.getMonth());
        int daysInPayedMonth = yearMonth.lengthOfMonth();
        int expirationDaysPeriod = 5;

        int greenStatusLimit = daysInPayedMonth - expirationDaysPeriod;

        if (daysBetween < greenStatusLimit) {
            return State.green(payment);
        } else if (daysBetween < daysInPayedMonth) {
            return State.orange(daysInPayedMonth - daysBetween);
        }

        return State.red();
    }

    @Override
    public void purge() {
        membershipRepo.deleteAll();
    }
}
