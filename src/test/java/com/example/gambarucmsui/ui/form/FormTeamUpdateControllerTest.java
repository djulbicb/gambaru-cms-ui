//package com.example.gambarucmsui.ui.form;
//
//import com.example.gambarucmsui.H2DatabaseConfig;
//import com.example.gambarucmsui.common.Messages;
//import com.example.gambarucmsui.database.entity.TeamEntity;
//import com.example.gambarucmsui.ports.ValidatorResponse;
//import com.example.gambarucmsui.ui.form.validation.TeamInputValidator;
//import org.junit.jupiter.api.Test;
//
//import java.math.BigDecimal;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//class FormTeamUpdateControllerTest extends H2DatabaseConfig {
//    @Test
//    void shouldUpdateTeam() {
//        // given
//        TeamEntity en = teamSavePort.save("Lowe", BigDecimal.ZERO);
//        FormTeamUpdateController controller = new FormTeamUpdateController(en.getTeamId(), en.getName(), en.getMembershipPayment());
//
//        ValidatorResponse res = controller.updateOrReturnErrors(en.getTeamId(), "Change", String.valueOf(10.0));
//
//        TeamEntity updated = teamLoad.findById(en.getTeamId()).get();
//
//        assertTrue(res.isOk());
//        assertEquals("Change", updated.getName());
//        assertEquals(BigDecimal.valueOf(10.0), updated.getMembershipPayment());
//    }
//
//    @Test
//    void shouldNotUpdateTeamCauseDuplicateTeamName() {
//        // given
//        TeamEntity preExisting = teamSavePort.save("preExisting", BigDecimal.ZERO);
//
//        // when
//        TeamEntity en = teamSavePort.save("Lowe", BigDecimal.ZERO);
//        FormTeamUpdateController controller = new FormTeamUpdateController(en.getTeamId(), en.getName(), en.getMembershipPayment());
//
//        ValidatorResponse res = controller.updateOrReturnErrors(en.getTeamId(), "preExisting", String.valueOf(10.0));
//
//        // then
//        TeamEntity updated = teamLoad.findById(en.getTeamId()).get();
//        assertTrue(res.hasErrors());
//        assertEquals(res.getErrorOrEmpty("name"), Messages.TEAM_NAME_ALREADY_EXISTS);
//        assertEquals("Lowe", updated.getName());
//    }
//
//    @Test
//    public void shouldNotSaveTeamCauseWrongName() {
//        // given
//        TeamEntity given = teamSavePort.save("Lowe", BigDecimal.ZERO);
//        FormTeamUpdateController formTeamAddController = new FormTeamUpdateController(given.getTeamId(), given.getName(), given.getMembershipPayment());
//
//        // when
//        ValidatorResponse save1 = formTeamAddController.updateOrReturnErrors(given.getTeamId(), null, "123");
//        ValidatorResponse save2 = formTeamAddController.updateOrReturnErrors(given.getTeamId(),"  ", "123");
//        ValidatorResponse save3 = formTeamAddController.updateOrReturnErrors(given.getTeamId(),"", "123");
//
//        // then
//        assertTrue(save1.hasErrors());
//        assertTrue(save2.hasErrors());
//        assertTrue(save3.hasErrors());
//
//        assertEquals(save1.getErrorOrEmpty("name"), Messages.TEAM_NAME_NOT_VALID);
//        assertEquals(save2.getErrorOrEmpty("name"), Messages.TEAM_NAME_NOT_VALID);
//        assertEquals(save3.getErrorOrEmpty("name"), Messages.TEAM_NAME_NOT_VALID);
//
//        TeamEntity loadAfterSave = teamLoad.findById(given.getTeamId()).get();
//        assertEquals(loadAfterSave, given);
//    }
//
//    @Test
//    public void shouldNotSaveTeamCauseWrongMembershipFee() {
//        // given
//        TeamEntity given = teamSavePort.save("Lowe", BigDecimal.ZERO);
//        FormTeamUpdateController formTeamAddController = new FormTeamUpdateController(given.getTeamId(), given.getName(), given.getMembershipPayment());
//
//        // when
//        ValidatorResponse save1 = formTeamAddController.updateOrReturnErrors(given.getTeamId(), "Star", null);
//        ValidatorResponse save2 = formTeamAddController.updateOrReturnErrors(given.getTeamId(), "Star", "   ");
//        ValidatorResponse save3 = formTeamAddController.updateOrReturnErrors(given.getTeamId(), "Star", "");
//
//        // then
//        assertTrue(save1.hasErrors());
//        assertTrue(save2.hasErrors());
//        assertTrue(save3.hasErrors());
//
//        assertEquals(Messages.TEAM_FEE_NOT_VALID, save1.getErrorOrEmpty("membershipPayment"));
//        assertEquals(Messages.TEAM_FEE_NOT_VALID, save2.getErrorOrEmpty("membershipPayment"));
//        assertEquals(Messages.TEAM_FEE_NOT_VALID, save3.getErrorOrEmpty("membershipPayment"));
//
//        TeamEntity loadAfterSave = teamLoad.findById(given.getTeamId()).get();
//        assertEquals(loadAfterSave, given);
//
//    }
//}