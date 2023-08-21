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
}