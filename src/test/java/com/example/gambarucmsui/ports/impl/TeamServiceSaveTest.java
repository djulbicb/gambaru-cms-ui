package com.example.gambarucmsui.ports.impl;

import com.example.gambarucmsui.H2Database;
import com.example.gambarucmsui.database.entity.TeamEntity;
import com.example.gambarucmsui.ports.Container;
import com.example.gambarucmsui.ports.interfaces.team.TeamIfExists;
import com.example.gambarucmsui.ports.interfaces.team.TeamSavePort;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TeamServiceSaveTest extends H2Database {

    private TeamIfExists teamIfExists;
    private TeamSavePort teamSavePort;

    @BeforeEach
    public void beforeEach() {
        teamIfExists = Container.getBean(TeamIfExists.class);
        teamSavePort = Container.getBean(TeamSavePort.class);
    }

    @AfterEach()
    public void purge(){
        delete(TeamEntity.class);
    }

    @Test
    void shouldCheckIfTeamNameExists() {
        // given
        teamSavePort.save("Team 1", BigDecimal.ONE);
        teamSavePort.save("Team 2", BigDecimal.ONE);

        // then
        assertTrue(teamIfExists.ifTeamNameExists("Team 1"));
        assertFalse(teamIfExists.ifTeamNameExists("Team 3"));
    }
    @Test
    void shouldCheckIfTeamDoesntExist() {
        // given
        teamSavePort.save("Team 1", BigDecimal.ONE);

        // then
        assertFalse(teamIfExists.ifTeamNameExists("Team 2"));
    }
}