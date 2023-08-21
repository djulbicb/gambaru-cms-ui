//package com.example.gambarucmsui.ui.form;
//
//import com.example.gambarucmsui.H2DatabaseConfig;
//import com.example.gambarucmsui.common.Messages;
//import com.example.gambarucmsui.ports.ValidatorResponse;
//import com.example.gambarucmsui.ui.form.validation.TeamInputValidator;
//import org.junit.jupiter.api.Test;
//
//import java.math.BigDecimal;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//class FormTeamAddControllerTest extends H2DatabaseConfig {
//    @Test
//    public void shouldSaveTeam() {
//        FormTeamAddController formTeamAddController =  new FormTeamAddController(teamIfExists, teamSavePort);
//        // when
//        ValidatorResponse save = formTeamAddController.saveOrReturnErrors("Lowe", "123");
//
//        // then
//        assertTrue(save.isOk());
//        assertEquals(Messages.TEAM_IS_CREATED("Lowe"), save.getMessage());
//    }
//
//    @Test
//    public void shouldNotSaveTeamCauseDuplicateTeamName() {
//        FormTeamAddController formTeamAddController =  new FormTeamAddController(teamIfExists, teamSavePort);
//        teamSavePort.save("Star", BigDecimal.valueOf(123));
//        // when
//        ValidatorResponse save = formTeamAddController.saveOrReturnErrors("Star", "123");
//
//        // then
//        assertTrue(save.hasErrors());
//        assertEquals(save.getErrorOrEmpty("name"), Messages.TEAM_NAME_ALREADY_EXISTS);
//    }
//
//    @Test
//    public void shouldNotSaveTeamCauseWrongName() {
//        FormTeamAddController formTeamAddController =  new FormTeamAddController(teamIfExists, teamSavePort);
//        // when
//        ValidatorResponse save1 = formTeamAddController.saveOrReturnErrors(null, "123");
//        ValidatorResponse save2 = formTeamAddController.saveOrReturnErrors("  ", "123");
//        ValidatorResponse save3 = formTeamAddController.saveOrReturnErrors("", "123");
//
//        // then
//        assertTrue(save1.hasErrors());
//        assertTrue(save2.hasErrors());
//        assertTrue(save3.hasErrors());
//
//        assertEquals(save1.getErrorOrEmpty("name"), Messages.TEAM_NAME_NOT_VALID);
//        assertEquals(save2.getErrorOrEmpty("name"), Messages.TEAM_NAME_NOT_VALID);
//        assertEquals(save3.getErrorOrEmpty("name"), Messages.TEAM_NAME_NOT_VALID);
//    }
//
//    @Test
//    public void shouldNotSaveTeamCauseWrongMembershipFee() {
//        FormTeamAddController formTeamAddController =  new FormTeamAddController(teamIfExists, teamSavePort);
//        // when
//        ValidatorResponse save1 = formTeamAddController.saveOrReturnErrors("Star", null);
//        ValidatorResponse save2 = formTeamAddController.saveOrReturnErrors("Star", "   ");
//        ValidatorResponse save3 = formTeamAddController.saveOrReturnErrors("Star", "");
//
//        // then
//        assertTrue(save1.hasErrors());
//        assertTrue(save2.hasErrors());
//        assertTrue(save3.hasErrors());
//
//        assertEquals(Messages.TEAM_FEE_NOT_VALID, save1.getErrorOrEmpty("membershipPayment"));
//        assertEquals(Messages.TEAM_FEE_NOT_VALID, save2.getErrorOrEmpty("membershipPayment"));
//        assertEquals(Messages.TEAM_FEE_NOT_VALID, save3.getErrorOrEmpty("membershipPayment"));
//    }
//
//}