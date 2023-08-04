package com.example.gambarucmsui.ports;

import com.example.gambarucmsui.database.entity.TeamEntity;
import com.example.gambarucmsui.database.repo.TeamRepository;
import com.example.gambarucmsui.ports.user.TeamSavePort;
import com.example.gambarucmsui.ports.user.TeamUpdatePort;

import java.math.BigDecimal;
import java.util.Optional;

public class TeamServiceSave implements TeamSavePort, TeamUpdatePort {

    private final TeamRepository teamRepository;

    public TeamServiceSave(TeamRepository teamRepository) {
        this.teamRepository = teamRepository;
    }

    @Override
    public Response<TeamEntity> save(String teamName, BigDecimal membershipFee) {
        if (teamRepository.ifTeamNameExists(teamName)) {
            return Response.bad(String.format("Tim sa imenom %s već postoji.", teamName));
        }
        TeamEntity en = new TeamEntity(teamName, TeamEntity.Status.ACTIVE, membershipFee);
        return Response.ok(String.format("Tim %s je uspešno sačuvan.", teamName), teamRepository.save(en));
    }
    
    @Override
    public Response<TeamEntity> updateTeam(Long teamId, String teamName, BigDecimal membershipFee) {
        Optional<TeamEntity> byId = teamRepository.findById(teamId);
        if (byId.isPresent()) {
            TeamEntity en = byId.get();

            // team name was changed, check if new name already exists
            if (!en.getName().equals(teamName) && teamRepository.ifTeamNameExists(teamName)) {
                return Response.bad("Tim sa tim imenom već postoji.");
            }

            en.setName(teamName);
            en.setMembershipPayment(membershipFee);
            teamRepository.save(en);

            return Response.ok(String.format("Tim %s je uspešno updejtovan.", teamName), teamRepository.save(en));
        }
        return Response.bad("Došlo je do neočekivane situacije. Navedeni tim ne postoji.");
    }
}

