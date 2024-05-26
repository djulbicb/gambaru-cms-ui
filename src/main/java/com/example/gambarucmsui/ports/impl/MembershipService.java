package com.example.gambarucmsui.ports.impl;

import com.example.gambarucmsui.database.entity.BarcodeEntity;
import com.example.gambarucmsui.database.entity.PersonMembershipEntity;
import com.example.gambarucmsui.database.repo.BarcodeRepository;
import com.example.gambarucmsui.database.repo.PersonMembershipRepository;
import com.example.gambarucmsui.ports.interfaces.membership.AddMembershipForBarcode;
import com.example.gambarucmsui.ports.interfaces.membership.LoadPersonMembership;
import com.example.gambarucmsui.util.DataUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;

public class MembershipService implements AddMembershipForBarcode, LoadPersonMembership {

    private final PersonMembershipRepository personMembershipRepository;
    private final BarcodeRepository barcodeRepo;

    public MembershipService(
            BarcodeRepository barcodeRepo,
            PersonMembershipRepository personMembershipRepository) {
        this.barcodeRepo = barcodeRepo;
        this.personMembershipRepository = personMembershipRepository;
    }

    @Override
    public Optional<PersonMembershipEntity> addMembership(Long barcodeId, LocalDateTime timestamp, int fee) {
        int compactDate = DataUtil.getCompactDate(timestamp);
        Optional<BarcodeEntity> byId = barcodeRepo.findById(barcodeId);
        if (byId.isPresent()) {
            PersonMembershipEntity membership = new PersonMembershipEntity( byId.get(), timestamp, compactDate, fee);
            personMembershipRepository.save(membership);
        }

        return Optional.empty();
    }

    @Override
    public List<PersonMembershipEntity> getAllMembershipForMonth(LocalDate date) {
        LocalDateTime startOfMonth = YearMonth.from(date).atDay(1).atStartOfDay();
        LocalDateTime endOfMonth = YearMonth.from(date).plusMonths(1).atDay(1).atStartOfDay();

        return personMembershipRepository.getAllMembershipsInRange(startOfMonth, endOfMonth);
    }
}
