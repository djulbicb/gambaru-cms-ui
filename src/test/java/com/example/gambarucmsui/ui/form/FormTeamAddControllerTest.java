package com.example.gambarucmsui.ui.form;

import com.example.gambarucmsui.H2DatabaseConfig;
import com.example.gambarucmsui.database.entity.TeamEntity;
import com.example.gambarucmsui.ports.Container;
import com.example.gambarucmsui.ports.ValidatorResponse;
import com.example.gambarucmsui.ports.interfaces.barcode.BarcodeFetchOrGeneratePort;
import com.example.gambarucmsui.ports.interfaces.barcode.BarcodeLoadPort;
import com.example.gambarucmsui.ports.interfaces.team.TeamIfExists;
import com.example.gambarucmsui.ports.interfaces.team.TeamLoadPort;
import com.example.gambarucmsui.ports.interfaces.team.TeamSavePort;
import com.example.gambarucmsui.ports.interfaces.user.UserAddToTeamPort;
import com.example.gambarucmsui.ports.interfaces.user.UserSavePort;
import com.example.gambarucmsui.ui.form.validation.TeamInputValidator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class FormTeamAddControllerTest extends H2DatabaseConfig {

    private FormTeamAddController formTeamAddController;
    private TeamSavePort teamSavePort;
    private TeamIfExists teamIfExists;

    @BeforeEach
    public void set() {
        teamSavePort = Container.getBean(TeamSavePort.class);
        teamIfExists = Container.getBean(TeamIfExists.class);
        formTeamAddController = new FormTeamAddController(teamIfExists, teamSavePort);
    }

    @Test
    public void shouldSaveTeam() {
        // when
        ValidatorResponse save = formTeamAddController.saveOrReturnErrors("Lowe", "123");

        // then
        assertTrue(save.isOk());
        assertEquals(TeamInputValidator.msgTeamIsCreated("Lowe"), save.getMessage());
    }

    @Test
    public void shouldNotSaveTeamCauseDuplicateTeamName() {
        teamSavePort.save("Star", BigDecimal.valueOf(123));
        // when
        ValidatorResponse save = formTeamAddController.saveOrReturnErrors("Star", "123");

        // then
        assertTrue(save.hasErrors());
        assertEquals(save.getErrors().get("name"), TeamInputValidator.errTeamNameExists());
    }

    @Test
    public void shouldNotSaveTeamCauseWrongName() {
        // when
        ValidatorResponse save1 = formTeamAddController.saveOrReturnErrors(null, "123");
        ValidatorResponse save2 = formTeamAddController.saveOrReturnErrors("  ", "123");
        ValidatorResponse save3 = formTeamAddController.saveOrReturnErrors("", "123");

        // then
        assertTrue(save1.hasErrors());
        assertTrue(save2.hasErrors());
        assertTrue(save3.hasErrors());

        assertEquals(save1.getErrors().get("name"), TeamInputValidator.errTeamName());
        assertEquals(save2.getErrors().get("name"), TeamInputValidator.errTeamName());
        assertEquals(save3.getErrors().get("name"), TeamInputValidator.errTeamName());
    }

    @Test
    public void shouldNotSaveTeamCauseWrongMembershipFee() {
        // when
        ValidatorResponse save1 = formTeamAddController.saveOrReturnErrors("Star", null);
        ValidatorResponse save2 = formTeamAddController.saveOrReturnErrors("Star", "   ");
        ValidatorResponse save3 = formTeamAddController.saveOrReturnErrors("Star", "");

        // then
        assertTrue(save1.hasErrors());
        assertTrue(save2.hasErrors());
        assertTrue(save3.hasErrors());

        assertEquals(TeamInputValidator.errTeamFee(), save1.getErrors().get("membershipPayment"));
        assertEquals(TeamInputValidator.errTeamFee(), save2.getErrors().get("membershipPayment"));
        assertEquals(TeamInputValidator.errTeamFee(), save3.getErrors().get("membershipPayment"));
    }

}