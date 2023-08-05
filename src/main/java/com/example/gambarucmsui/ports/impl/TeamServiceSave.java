package com.example.gambarucmsui.ports.impl;

import com.example.gambarucmsui.database.entity.TeamEntity;
import com.example.gambarucmsui.database.repo.TeamRepository;
import com.example.gambarucmsui.ports.Response;
import com.example.gambarucmsui.ports.interfaces.team.IsTeamExists;
import com.example.gambarucmsui.ports.interfaces.team.TeamLoadPort;
import com.example.gambarucmsui.ports.interfaces.team.TeamSavePort;
import com.example.gambarucmsui.ports.interfaces.team.TeamUpdatePort;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class TeamServiceSave implements TeamLoadPort, TeamSavePort, TeamUpdatePort, IsTeamExists {

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
    public Response<TeamEntity> updateTeam(Long teamId, String teamName, BigDecimal membershipFee, TeamEntity.Status status) {
        Optional<TeamEntity> byId = teamRepository.findById(teamId);
        if (byId.isPresent()) {
            TeamEntity en = byId.get();

            // team name was changed, check if new name already exists
            if (!en.getName().equals(teamName) && teamRepository.ifTeamNameExists(teamName)) {
                return Response.bad("Tim sa tim imenom već postoji.");
            }

            if (teamName != null) {
                en.setName(teamName);
            }
            if (membershipFee != null) {
                en.setMembershipPayment(membershipFee);
            }
            if (teamName != null) {
                en.setName(teamName);
            }
            if (status != null) {
                en.setStatus(status);
            }
            teamRepository.save(en);

            return Response.ok(String.format("Tim %s je uspešno updejtovan.", teamName), teamRepository.save(en));
        }
        return Response.bad("Došlo je do neočekivane situacije. Navedeni tim ne postoji.");
    }

    @Override
    public List<TeamEntity> findAllActive() {
        return teamRepository.findAllActive();
    }

    @Override
    public TeamEntity findByName(String teamName) {
        return teamRepository.findByName(teamName);
    }

    @Override
    public Optional<TeamEntity> findById(Long teamId) {
        return teamRepository.findById(teamId);
    }

    @Override
    public boolean ifTeamNameExists(String teamName) {
        return teamRepository.ifTeamNameExists(teamName);
    }
}

