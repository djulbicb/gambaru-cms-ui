package com.example.gambarucmsui.ports.interfaces.team;

import com.example.gambarucmsui.H2DatabaseConfig;
import com.example.gambarucmsui.database.entity.BarcodeEntity;
import com.example.gambarucmsui.database.entity.TeamEntity;
import com.example.gambarucmsui.ports.ValidatorResponse;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TeamDeletePortTest extends H2DatabaseConfig {
    @Test
    public void shouldDeleteTeamAndSetBarcodesToDeletedStatus() throws IOException {
        BarcodeEntity barcode = scenario_AssignPersonToTeamAndReturnAssignedBarcode("First", "Last", "Team");

        ValidatorResponse res = teamDeletePort.validateAndDeleteTeam(barcode.getTeam().getTeamId());
        assertTrue(res.isOk());

        TeamEntity team = teamLoad.findById(barcode.getTeam().getTeamId()).get();

        assertEquals("Team(Obrisan)", team.getName());
        assertEquals(TeamEntity.Status.DELETED, team.getStatus());
        assertEquals(BarcodeEntity.Status.DELETED, barcode.getStatus());
    }

    @Test
    public void shouldDeleteTeamAndIgnoreItWhenSearchingAllActiveTeams() throws IOException {
        // given
        TeamEntity team1 = teamSavePort.save("Team1", BigDecimal.ONE);
        TeamEntity team2 = teamSavePort.save("Team2", BigDecimal.ONE);

        // when
        teamDeletePort.validateAndDeleteTeam(team1.getTeamId());

        // then
        List<TeamEntity> active = teamLoad.findAllActive();
        assertEquals(active, List.of(team2));
    }
}


//    @Test
//    public void shouldDeleteTeamAndIgnoreItWhenSearchingAllActiveTeams() throws IOException {
//        // given
//        scenario_AssignPersonToTeamAndReturnAssignedBarcode("asd", "sdf", "team1");
//        scenario_AssignPersonToTeamAndReturnAssignedBarcode("zxc", "xcv", "team2");
//
//        // when
//        TeamEntity team1 = teamLoad.findByName("team1");
//        TeamEntity team2 = teamLoad.findByName("team2");
//        teamDeletePort.validateAndDeleteTeam(team1.getTeamId());
//
//        // then
//        List<TeamEntity> active = teamLoad.findAllActive();
//        assertEquals(active, List.of(team2));
//    }