package com.example.gambarucmsui.ports.interfaces.attendance;

import com.example.gambarucmsui.H2DatabaseConfig;
import com.example.gambarucmsui.database.entity.BarcodeEntity;
import com.example.gambarucmsui.database.entity.PersonEntity;
import com.example.gambarucmsui.database.entity.TeamEntity;
import com.example.gambarucmsui.ports.Container;
import com.example.gambarucmsui.ports.ValidatorResponse;
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

import static com.example.gambarucmsui.TestData.NOW_DATE_TIME;
import static com.example.gambarucmsui.TestData.dummyBarcode;
import static com.example.gambarucmsui.common.Messages.*;
import static org.junit.jupiter.api.Assertions.*;

class AttendanceAddForUserPortTest extends H2DatabaseConfig {
    private AttendanceLoadForUserPort attendanceLoadForUserPort;
    private AttendanceAddForUserPort attendanceAddForUserPort;
    private UserAddToTeamPort userAddToTeam;
    private BarcodeFetchOrGeneratePort barcodeFetchOrGenerate;
    private UserSavePort userSave;
    private TeamSavePort teamSave;
    private BarcodeLoadPort barcodeLoad;
    private TeamLoadPort teamLoad;
    private TeamDeletePort teamDeletePort;
    private BarcodeStatusChangePort barcodeStatusChangePort;

    @BeforeEach
    public void set() {
        attendanceLoadForUserPort = Container.getBean(AttendanceLoadForUserPort.class);
        attendanceAddForUserPort = Container.getBean(AttendanceAddForUserPort.class);
        barcodeFetchOrGenerate = Container.getBean(BarcodeFetchOrGeneratePort.class);
        barcodeStatusChangePort = Container.getBean(BarcodeStatusChangePort.class);
        userAddToTeam = Container.getBean(UserAddToTeamPort.class);
        userSave = Container.getBean(UserSavePort.class);
        teamSave = Container.getBean(TeamSavePort.class);
        barcodeLoad = Container.getBean(BarcodeLoadPort.class);
        teamLoad = Container.getBean(TeamLoadPort.class);
        teamDeletePort = Container.getBean(TeamDeletePort.class);
    }

    @Test
    public void shouldAddAttendanceForUser() throws IOException {
        BarcodeEntity barcode = scenario_AssignPersonToTeamAndReturnAssignedBarcode();

        ValidatorResponse res = attendanceAddForUserPort.validateAndAddAttendance(barcode.getBarcodeId(), NOW_DATE_TIME);

        assertEquals("Bo Lowe prisutan.", res.getMessage());
        assertTrue(res.isOk());
    }

    @Test
    public void shouldFailCauseBarcodeDeactivated() throws IOException {
        // given
        BarcodeEntity barcode = scenario_AssignPersonToTeamAndReturnAssignedBarcode();

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
        BarcodeEntity barcode = scenario_AssignPersonToTeamAndReturnAssignedBarcode();

        // Add attendance before team deletion
        ValidatorResponse resBeforeDeletion = attendanceAddForUserPort.validateAndAddAttendance(barcode.getBarcodeId(), NOW_DATE_TIME);
        assertTrue(resBeforeDeletion.isOk());
        assertEquals("Bo Lowe prisutan.", resBeforeDeletion.getMessage());

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

    @NotNull
    private BarcodeEntity scenario_AssignPersonToTeamAndReturnAssignedBarcode() throws IOException {
        BarcodeEntity barcode = barcodeFetchOrGenerate.fetchOneOrGenerate(BarcodeEntity.Status.NOT_USED);
        PersonEntity user = userSave.save("Bo", "Lowe", PersonEntity.Gender.MALE, "123", null);
        TeamEntity team = teamSave.save("Lowe", BigDecimal.valueOf(123));
        userAddToTeam.addUserToPort(user.getPersonId(), barcode.getBarcodeId(), team.getName());
        return barcode;
    }
}