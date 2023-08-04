package com.example.gambarucmsui.ports;

import com.example.gambarucmsui.database.entity.TeamEntity;
import com.example.gambarucmsui.database.repo.TeamRepository;
import com.example.gambarucmsui.ports.user.TeamSavePort;
import com.example.gambarucmsui.ports.user.TeamUpdatePort;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TeamServiceSave implements TeamSavePort, TeamUpdatePort {

    private final TeamRepository teamRepository;

    public TeamServiceSave(TeamRepository teamRepository) {
        this.teamRepository = teamRepository;
    }

    @Override
    public Response<TeamEntity> saveTeam(String teamName, BigDecimal membershipFee) {
        if (teamRepository.ifTeamNameExists(teamName)) {
            return Response.bad("Tim sa tim imenom već postoji");
        }
        TeamEntity en = new TeamEntity(teamName, TeamEntity.Status.ACTIVE, membershipFee);
        return Response.ok("Tim je sacuvan", teamRepository.save(en));
    }

    private List<TeamEntity> teamEntities = new ArrayList<>();
    @Override
    public void addToBulkSave(String teamName, BigDecimal membershipFee) {
        teamEntities.add(new TeamEntity(teamName, TeamEntity.Status.ACTIVE, membershipFee));
    }

    @Override
    public void executeBulkSave() {
        teamRepository.saveAll(teamEntities);
        teamEntities.clear();
    }

    @Override
    public boolean updateTeam(Long teamId, String team, BigDecimal membershipFee) {
        Optional<TeamEntity> byId = teamRepository.findById(teamId);
        if (byId.isPresent()) {
            TeamEntity en = byId.get();
            en.setName(team);
            en.setMembershipPayment(membershipFee);
            teamRepository.save(en);
            return true;
        }
        return false;
    }
}

