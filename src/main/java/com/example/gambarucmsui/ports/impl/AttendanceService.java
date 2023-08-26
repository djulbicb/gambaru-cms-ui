package com.example.gambarucmsui.ports.impl;

import com.example.gambarucmsui.database.entity.*;
import com.example.gambarucmsui.database.repo.BarcodeRepository;
import com.example.gambarucmsui.database.repo.UserAttendanceRepository;
import com.example.gambarucmsui.ports.ValidatorResponse;
import com.example.gambarucmsui.ports.interfaces.attendance.AttendanceAddForUserPort;
import com.example.gambarucmsui.ports.interfaces.attendance.AttendanceLoadForUserPort;
import com.example.gambarucmsui.ports.interfaces.attendance.AttendancePurgePort;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static com.example.gambarucmsui.common.Messages.*;
import static com.example.gambarucmsui.database.entity.BarcodeEntity.BARCODE_ID;
import static com.example.gambarucmsui.util.FormatUtil.isLong;
import static com.example.gambarucmsui.util.FormatUtil.parseBarcodeStr;
import static com.example.gambarucmsui.util.LayoutUtil.getOr;

public class AttendanceService implements AttendanceAddForUserPort, AttendanceLoadForUserPort, AttendancePurgePort {

    private final UserAttendanceRepository attendanceRepo;
    private final BarcodeRepository barcodeRepo;

    public AttendanceService(
            BarcodeRepository barcodeRepo,
            UserAttendanceRepository attendanceRepo) {
        this.barcodeRepo = barcodeRepo;
        this.attendanceRepo = attendanceRepo;
    }

    @Override
    public ValidatorResponse validateAddAttendance(String barcodeIdStr) {
        Map<String, String> errors = new HashMap<>();
        if (!isLong(barcodeIdStr)) {
            if (barcodeIdStr.isBlank()) {
                errors.put(BARCODE_ID, "");
            } else {
                errors.put(BARCODE_ID, BARCODE_WRONG_FORMAT);
            }
            return new ValidatorResponse(errors);
        }
        Long barcode = parseBarcodeStr(barcodeIdStr);
        Optional<BarcodeEntity> barcodeEntityOptional = barcodeRepo.findById(barcode);
        if (barcodeEntityOptional.isEmpty()) {
            errors.put(BARCODE_ID, BARCODE_NOT_REGISTERED);
            return new ValidatorResponse(errors);
        }
        BarcodeEntity b = barcodeEntityOptional.get();
        if (b.getStatus() == BarcodeEntity.Status.DELETED)  {
            errors.put(BARCODE_ID, BARCODE_IS_DELETED);
            return new ValidatorResponse(errors);
        }
        if (b.getStatus() == BarcodeEntity.Status.NOT_USED)  {
            errors.put(BARCODE_ID, BARCODE_IS_NOT_ASSIGNED);
            return new ValidatorResponse(errors);
        }
        if (b.getStatus() == BarcodeEntity.Status.DEACTIVATED)  {
            errors.put(BARCODE_ID, BARCODE_IS_DEACTIVATED);
            return new ValidatorResponse(errors);
        }
        PersonEntity user = b.getPerson();
        return new ValidatorResponse(ATTENDANCE_MANUALLY_FOUND_USER(user.getFirstName(), user.getLastName()));
    }

    @Override
    public ValidatorResponse validateAndAddAttendance(Long barcodeId, LocalDateTime timestamp) {
        ValidatorResponse res = validateAddAttendance(String.valueOf(barcodeId));
        if (res.hasErrors()) {
            return res;
        }

        BarcodeEntity barcode = barcodeRepo.findById(barcodeId).get();
        attendanceRepo.save(new PersonAttendanceEntity(barcode, timestamp));

        PersonEntity person = barcode.getPerson();
        return new ValidatorResponse(String.format("%s %s prisutan.", person.getFirstName(), person.getLastName()));
    }

    List<PersonAttendanceEntity> attendanceEntityList = new ArrayList<>();
    @Override
    public void addToSaveBulk(BarcodeEntity barcode, LocalDateTime timestamp) {
        attendanceEntityList.add(new PersonAttendanceEntity(barcode, timestamp));
    }
    @Override
    public void executeBulk() {
        attendanceRepo.saveAll(attendanceEntityList);
        attendanceEntityList.clear();
    }

    @Override
    public List<PersonAttendanceEntity> findAllForAttendanceDate(LocalDate forDate) {
        return attendanceRepo.findAllForAttendanceDate(forDate);
    }

    @Override
    public List<PersonAttendanceEntity> fetchLastNEntriesForUserAttendance(List<BarcodeEntity> barcodeIds, int count) {
        return attendanceRepo.fetchLastNEntriesForUserAttendance(barcodeIds, count);
    }

    @Override
    public void purge() {
        attendanceRepo.deleteAll();
    }
}
