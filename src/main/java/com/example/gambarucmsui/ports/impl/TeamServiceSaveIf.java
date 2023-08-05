package com.example.gambarucmsui.ports.impl;

import com.example.gambarucmsui.database.entity.TeamEntity;
import com.example.gambarucmsui.database.repo.TeamRepository;
import com.example.gambarucmsui.ports.Response;
import com.example.gambarucmsui.ports.ValidatorResponse;
import com.example.gambarucmsui.ports.interfaces.team.TeamIfExists;
import com.example.gambarucmsui.ports.interfaces.team.TeamLoadPort;
import com.example.gambarucmsui.ports.interfaces.team.TeamSavePort;
import com.example.gambarucmsui.ports.interfaces.team.TeamUpdatePort;
import com.example.gambarucmsui.ui.form.validation.TeamInputValidator;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class TeamServiceSaveIf implements TeamLoadPort, TeamSavePort, TeamUpdatePort, TeamIfExists {

    private final TeamRepository teamRepository;
    private final TeamInputValidator teamValidator = new TeamInputValidator();

    public TeamServiceSaveIf(TeamRepository teamRepository) {
        this.teamRepository = teamRepository;
    }

    @Override
    public ValidatorResponse verifySaveTeam(String teamName, String fee) {
        Map<String, String> errors = new HashMap<>();
        if (!teamValidator.isTeamNameValid(teamName)) {
            errors.put("name", TeamInputValidator.errTeamName());
        }
        if (!teamValidator.isFeeValid(fee)) {
            errors.put("membershipPayment", TeamInputValidator.errTeamFee());
        }
        if (ifTeamNameExists(teamName)) {
            errors.put("name", TeamInputValidator.errTeamNameExists());
        }
        return new ValidatorResponse(errors);
    }

    @Override
    public TeamEntity save(String teamName, BigDecimal membershipFee) {
        TeamEntity en = new TeamEntity(teamName, TeamEntity.Status.ACTIVE, membershipFee);
        return teamRepository.save(en);
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

