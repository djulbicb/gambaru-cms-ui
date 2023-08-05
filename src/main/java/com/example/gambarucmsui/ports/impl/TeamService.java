package com.example.gambarucmsui.ports.impl;

import com.example.gambarucmsui.database.entity.TeamEntity;
import com.example.gambarucmsui.database.repo.TeamRepository;
import com.example.gambarucmsui.ports.ValidatorResponse;
import com.example.gambarucmsui.ports.interfaces.team.*;
import com.example.gambarucmsui.ui.form.validation.TeamInputValidator;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class TeamService implements TeamLoadPort, TeamSavePort, TeamUpdatePort, TeamIfExists, TeamPurgePort {

    private final TeamRepository teamRepository;
    private final TeamInputValidator teamValidator = new TeamInputValidator();

    public TeamService(TeamRepository teamRepository) {
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
    public ValidatorResponse verifyUpdateTeam(Long teamId, String teamName, String fee) {
        Optional<TeamEntity> byId = findById(teamId);
        TeamEntity team = byId.get();

        Map<String, String> errors = new HashMap<>();
        if (!teamValidator.isTeamNameValid(teamName)) {
            errors.put("name", TeamInputValidator.errTeamName());
        }
        if (!teamValidator.isFeeValid(fee)) {
            errors.put("membershipPayment", TeamInputValidator.errTeamFee());
        }

        if (!team.getName().equals(teamName) && ifTeamNameExists(teamName)) {
            errors.put("name", TeamInputValidator.errTeamNameExists());
        }

        return new ValidatorResponse(errors);
    }

    @Override
    public TeamEntity updateTeam(Long teamId, String teamName, BigDecimal membershipFee, TeamEntity.Status status) {
        Optional<TeamEntity> byId = teamRepository.findById(teamId);
        if (byId.isPresent()) {
            TeamEntity en = byId.get();

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
            return teamRepository.save(en);
        }
        throw new RuntimeException("No such team with id:" + teamId);
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

    @Override
    public void purge() {
        teamRepository.deleteAll();
    }
}

