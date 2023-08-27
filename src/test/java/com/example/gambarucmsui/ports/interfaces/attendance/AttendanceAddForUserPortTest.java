package com.example.gambarucmsui.ports.interfaces.attendance;

import com.example.gambarucmsui.H2DatabaseConfig;
import com.example.gambarucmsui.database.entity.BarcodeEntity;
import com.example.gambarucmsui.ports.ValidatorResponse;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static com.example.gambarucmsui.TestData.NOW_DATE_TIME;
import static com.example.gambarucmsui.TestData.dummyBarcode;
import static com.example.gambarucmsui.common.Messages.*;
import static org.junit.jupiter.api.Assertions.*;

class AttendanceAddForUserPortTest extends H2DatabaseConfig {
    @Test
    public void shouldAddAttendanceForUser() throws IOException {
        BarcodeEntity barcode = scenario_AssignPersonToTeamAndReturnAssignedBarcode("First", "Last", "Team" );

        ValidatorResponse res = attendanceAddForUserPort.validateAndAddAttendance(barcode.getBarcodeId(), NOW_DATE_TIME);

        assertEquals("First Last prisutan.", res.getMessage());
        assertTrue(res.isOk());
    }

    @Test
    public void shouldFailCauseBarcodeDeactivated() throws IOException {
        // given
        BarcodeEntity barcode = scenario_AssignPersonToTeamAndReturnAssignedBarcode("First", "Name", "Team");

        // Add attendance before team deletion
        ValidatorResponse resBeforeDeactivation = attendanceAddForUserPort.validateAndAddAttendance(barcode.getBarcodeId(), NOW_DATE_TIME);
        assertTrue(resBeforeDeactivation.isOk());

        // Deactivate barcode
        barcodeStatusChangePort.deactivateBarcode(barcode.getBarcodeId());

        // Add attendance before team deletion
        ValidatorResponse resAfterDeactivation = attendanceAddForUserPort.validateAndAddAttendance(barcode.getBarcodeId(), NOW_DATE_TIME);
        assertTrue(resAfterDeactivation.hasErrors());
        assertEquals(BARCODE_IS_DEACTIVATED, resAfterDeactivation.getErrorOrEmpty(BarcodeEntity.BARCODE_ID));
    }

    @Test
    public void shouldFailCauseTeamIsDeleted() throws IOException {
        // given
        BarcodeEntity barcode = scenario_AssignPersonToTeamAndReturnAssignedBarcode("First", "Name", "Team");

        // Add attendance before team deletion
        ValidatorResponse resBeforeDeletion = attendanceAddForUserPort.validateAndAddAttendance(barcode.getBarcodeId(), NOW_DATE_TIME);
        assertTrue(resBeforeDeletion.isOk());
        assertEquals("First Name prisutan.", resBeforeDeletion.getMessage());

        // delete team
        ValidatorResponse resDeletion = teamDeletePort.validateAndDeleteTeam(barcode.getTeam().getTeamId());
        assertTrue(resDeletion.isOk());

        // Add attendance after team deletion
        ValidatorResponse resAfterDeletion = attendanceAddForUserPort.validateAndAddAttendance(barcode.getBarcodeId(), NOW_DATE_TIME);
        assertTrue(resAfterDeletion.hasErrors());
        assertEquals(BARCODE_IS_DELETED, resAfterDeletion.getErrorOrEmpty(BarcodeEntity.BARCODE_ID));
    }

    @Test
    public void shouldValidationFailCauseNotExistingBarcode() throws IOException {
        BarcodeEntity barcode = dummyBarcode(2L);

        ValidatorResponse res = attendanceAddForUserPort.validateAndAddAttendance(barcode.getBarcodeId(), NOW_DATE_TIME);
        assertEquals(BARCODE_NOT_REGISTERED, res.getErrorOrEmpty(BarcodeEntity.BARCODE_ID));
    }
    @Test
    public void shouldValidationFailCauseBarcodeWrongText() throws IOException {
        ValidatorResponse res = attendanceAddForUserPort.validateAddAttendance("abc");
        assertEquals(BARCODE_WRONG_FORMAT, res.getErrorOrEmpty(BarcodeEntity.BARCODE_ID));
    }
    @Test
    public void shouldValidationFailCauseBarcodeNotUsed() throws IOException {
        BarcodeEntity barcode = barcodeFetchOrGenerate.fetchOneOrGenerate(BarcodeEntity.Status.NOT_USED);

        ValidatorResponse res = attendanceAddForUserPort.validateAndAddAttendance(barcode.getBarcodeId(), NOW_DATE_TIME);
        assertEquals(BARCODE_IS_NOT_ASSIGNED, res.getErrorOrEmpty(BarcodeEntity.BARCODE_ID));
    }

}