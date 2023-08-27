package com.example.gambarucmsui.ports.interfaces.team;

import com.example.gambarucmsui.H2DatabaseConfig;
import com.example.gambarucmsui.common.Messages;
import com.example.gambarucmsui.database.entity.TeamEntity;
import com.example.gambarucmsui.ports.ValidatorResponse;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TeamSavePortTest extends H2DatabaseConfig {
    @Test
    public void shouldVerifyAndCreateTeam() throws IOException {
        ValidatorResponse res = teamSavePort.verifyAndSaveTeam("Lowe", "123");
        assertTrue(res.isOk());

        TeamEntity team = teamLoad.findByName("Lowe");

        assertNotNull(team);
        assertEquals("Lowe", team.getName());
        assertEquals(BigDecimal.valueOf(123), team.getMembershipPayment());
        assertEquals(TeamEntity.Status.ACTIVE, team.getStatus());
    }

    @Test
    public void shouldFailVerifyAndNotCreateTeam() throws IOException {
        ValidatorResponse res1 = teamSavePort.verifyAndSaveTeam("", "");
        ValidatorResponse res2 = teamSavePort.verifyAndSaveTeam(" ", " ");
        ValidatorResponse res3 = teamSavePort.verifyAndSaveTeam(" ", "abc");
        ValidatorResponse res4 = teamSavePort.verifyAndSaveTeam(null, null);

        List<ValidatorResponse> responses = List.of(res1, res2, res3, res4);
        for (ValidatorResponse res : responses) {
            assertTrue(res.hasErrors());
            assertEquals(Messages.TEAM_NAME_NOT_VALID, res.getErrorOrEmpty(TeamEntity.TEAM_NAME));
            assertEquals(Messages.TEAM_FEE_NOT_VALID, res.getErrorOrEmpty(TeamEntity.MEMBERSHIP_PAYMENT));
        }
    }

    @Test
    public void shouldFailTestWhenTeamWithThanNameExists() throws IOException {
        ValidatorResponse res1 = teamSavePort.verifyAndSaveTeam("Lowe", "123");
        ValidatorResponse res2 = teamSavePort.verifyAndSaveTeam("Lowe", "234");

        assertTrue(res1.isOk());
        assertTrue(res2.hasErrors());

        assertEquals(Messages.TEAM_IS_CREATED("Lowe"), res1.getMessage());
        assertEquals(Messages.TEAM_NAME_ALREADY_EXISTS, res2.getErrorOrEmpty(TeamEntity.TEAM_NAME));
    }

    @Test
    public void shouldSuccesTestWhenTeamWithThanNameExistsButWasDeleted() throws IOException {
        teamSavePort.verifyAndSaveTeam("Lowe", "123");
        TeamEntity team = teamLoad.findByName("Lowe");
        teamDeletePort.validateAndDeleteTeam(team.getTeamId());

        ValidatorResponse res2 = teamSavePort.verifyAndSaveTeam("Lowe", "234");
        assertTrue(res2.isOk());
        assertEquals(Messages.TEAM_IS_CREATED("Lowe"), res2.getMessage());

        // Edgecase: After deletion team is renamed with sufix (Obrisan) and its deactivated
        // Technically, you can create an ACTIVE team with that name
        ValidatorResponse res3 = teamSavePort.verifyAndSaveTeam("Lowe(Obrisan)", "234");
        assertTrue(res3.isOk());
        assertEquals(Messages.TEAM_IS_CREATED("Lowe(Obrisan)"), res3.getMessage());

        List<TeamEntity> activeTeams = teamLoad.findAllActive();
        assertEquals(2, activeTeams.size());
    }
}