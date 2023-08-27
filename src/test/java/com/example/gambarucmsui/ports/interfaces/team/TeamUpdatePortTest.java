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

class TeamUpdatePortTest extends H2DatabaseConfig {
    @Test
    public void shouldVerifyAndUpdateTeam() throws IOException {
        teamSavePort.verifyAndSaveTeam("Lowe", "123");

        TeamEntity team = teamLoad.findByName("Lowe");
        ValidatorResponse res = teamUpdatePort.verifyAndUpdateTeam(team.getTeamId(), "Changed", "234");
        assertTrue(res.isOk());

        TeamEntity changed = teamLoad.findByName("Changed");
        assertNotNull(team);
        assertEquals(changed.getTeamId(), team.getTeamId());
        assertEquals("Changed", team.getName());
        assertEquals(BigDecimal.valueOf(234), team.getMembershipPayment());
        assertEquals(TeamEntity.Status.ACTIVE, team.getStatus());
    }

    @Test
    public void shouldFailVerifyAndNotUpdateTeam() throws IOException {
        teamSavePort.verifyAndSaveTeam("Lowe", "123");
        TeamEntity en = teamLoad.findByName("Lowe");

        ValidatorResponse res1 = teamUpdatePort.verifyAndUpdateTeam(en.getTeamId(), "", "");
        ValidatorResponse res2 = teamUpdatePort.verifyAndUpdateTeam(en.getTeamId()," ", " ");
        ValidatorResponse res3 = teamUpdatePort.verifyAndUpdateTeam(en.getTeamId()," ", "abc");
        ValidatorResponse res4 = teamUpdatePort.verifyAndUpdateTeam(en.getTeamId(),null, null);

        List<ValidatorResponse> responses = List.of(res1, res2, res3, res4);
        for (ValidatorResponse res : responses) {
            assertTrue(res.hasErrors());
            assertEquals(Messages.TEAM_NAME_NOT_VALID, res.getErrorOrEmpty(TeamEntity.TEAM_NAME));
            assertEquals(Messages.TEAM_FEE_NOT_VALID, res.getErrorOrEmpty(TeamEntity.MEMBERSHIP_PAYMENT));
        }
    }

    @Test
    public void shouldFailTestWhenTeamWithThanNameExists() throws IOException {
        // save
        teamSavePort.verifyAndSaveTeam("First", "123");
        teamSavePort.verifyAndSaveTeam("Second", "123");
        TeamEntity team = teamLoad.findByName("First");

        // update
        ValidatorResponse res1 = teamUpdatePort.verifyAndUpdateTeam(team.getTeamId(), "Update", "234");
        assertTrue(res1.isOk());
        assertEquals(Messages.TEAM_IS_UPDATED("Update"), res1.getMessage());

        // try to save again but fail
        ValidatorResponse res2 = teamUpdatePort.verifyAndUpdateTeam(team.getTeamId(), "Second", "234");
        assertTrue(res2.hasErrors());
        assertEquals(Messages.TEAM_NAME_ALREADY_EXISTS, res2.getErrorOrEmpty(TeamEntity.TEAM_NAME));
    }

    @Test
    public void shouldSuccessWhenUpdatingTeamWithSameName() throws IOException {
        teamSavePort.verifyAndSaveTeam("This", "123");
        TeamEntity team = teamLoad.findByName("This");

        ValidatorResponse res1 = teamUpdatePort.verifyAndUpdateTeam(team.getTeamId(), "This", "234");
        assertTrue(res1.isOk());
        assertEquals(Messages.TEAM_IS_UPDATED("This"), res1.getMessage());

        TeamEntity teamAfterUpdate = teamLoad.findByName("This");
        assertEquals("This", teamAfterUpdate.getName());
        assertEquals(new BigDecimal("234"), teamAfterUpdate.getMembershipPayment());
    }

}