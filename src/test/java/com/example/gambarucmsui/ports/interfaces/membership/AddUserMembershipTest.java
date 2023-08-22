package com.example.gambarucmsui.ports.interfaces.membership;

import com.example.gambarucmsui.H2DatabaseConfig;
import com.example.gambarucmsui.database.entity.BarcodeEntity;
import com.example.gambarucmsui.database.entity.PersonEntity;
import com.example.gambarucmsui.database.entity.TeamEntity;
import com.example.gambarucmsui.ports.Container;
import com.example.gambarucmsui.ports.ValidatorResponse;
import com.example.gambarucmsui.ports.interfaces.attendance.AttendanceAddForUserPort;
import com.example.gambarucmsui.ports.interfaces.attendance.AttendanceLoadForUserPort;
import com.example.gambarucmsui.ports.interfaces.barcode.BarcodeFetchOrGeneratePort;
import com.example.gambarucmsui.ports.interfaces.barcode.BarcodeLoadPort;
import com.example.gambarucmsui.ports.interfaces.barcode.BarcodeStatusChangePort;
import com.example.gambarucmsui.ports.interfaces.team.TeamDeletePort;
import com.example.gambarucmsui.ports.interfaces.team.TeamLoadPort;
import com.example.gambarucmsui.ports.interfaces.team.TeamSavePort;
import com.example.gambarucmsui.ports.interfaces.user.UserAddToTeamPort;
import com.example.gambarucmsui.ports.interfaces.user.UserSavePort;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.math.BigDecimal;

import static com.example.gambarucmsui.TestData.*;
import static com.example.gambarucmsui.common.Messages.MEMBERSHIP_ALREADY_PAYED;
import static com.example.gambarucmsui.common.Messages.MEMBERSHIP_PAYMENT_ADDED;
import static com.example.gambarucmsui.database.entity.BarcodeEntity.BARCODE_ID;
import static org.junit.jupiter.api.Assertions.*;

class AddUserMembershipTest extends H2DatabaseConfig {

    @Test
    public void shouldAddPayment() throws IOException {
        BarcodeEntity barcode = scenario_AssignPersonToTeamAndReturnAssignedBarcode("First", "Last", "Team");

        ValidatorResponse res = addUserMembership.validateAndAddMembership(String.valueOf(barcode.getBarcodeId()), NOW_DATE_TIME);

        PersonEntity p = barcode.getPerson();
        assertEquals(MEMBERSHIP_PAYMENT_ADDED(p.getFirstName(), p.getLastName()), res.getMessage());
        assertTrue(res.isOk());
    }
    @Test
    public void shouldNotAddPaymentCausePaymentAlreadyExists() throws IOException {
        BarcodeEntity barcode = scenario_AssignPersonToTeamAndReturnAssignedBarcode("First", "Last", "Team");

        ValidatorResponse resFirst = addUserMembership.validateAndAddMembership(String.valueOf(barcode.getBarcodeId()), NOW_DATE_TIME);
        ValidatorResponse resSecond = addUserMembership.validateAndAddMembership(String.valueOf(barcode.getBarcodeId()), NOW_DATE_TIME);

        assertTrue(resFirst.isOk());
        assertTrue(resSecond.hasErrors());
        assertEquals(MEMBERSHIP_ALREADY_PAYED, resSecond.getErrorOrEmpty(BARCODE_ID));
    }

    @Test
    public void shouldCheckMembershipPaymentMonthRange() throws IOException {
        BarcodeEntity barcode = scenario_AssignPersonToTeamAndReturnAssignedBarcode("First", "Name", "Team");
        PersonEntity p = barcode.getPerson();

        ValidatorResponse currentMonthStart = addUserMembership.validateAndAddMembership(String.valueOf(barcode.getBarcodeId()), FEBRUARY_START_DATE);
        assertTrue(currentMonthStart.isOk());
        assertEquals(MEMBERSHIP_PAYMENT_ADDED(p.getFirstName(), p.getLastName()), currentMonthStart.getMessage());

        ValidatorResponse currentMonthEnd = addUserMembership.validateAndAddMembership(String.valueOf(barcode.getBarcodeId()), FEBRUARY_END_DATE);
        assertTrue(currentMonthEnd.hasErrors());
        assertEquals(MEMBERSHIP_ALREADY_PAYED, currentMonthEnd.getErrorOrEmpty(BARCODE_ID));

        ValidatorResponse nextMonthStart = addUserMembership.validateAndAddMembership(String.valueOf(barcode.getBarcodeId()), MARCH_START_DATE);
        assertTrue(nextMonthStart.isOk());
        assertEquals(MEMBERSHIP_PAYMENT_ADDED(p.getFirstName(), p.getLastName()), nextMonthStart.getMessage());
    }
    }