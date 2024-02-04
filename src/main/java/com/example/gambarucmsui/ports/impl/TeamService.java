package com.example.gambarucmsui.ports.impl;

import com.example.gambarucmsui.common.Messages;
import com.example.gambarucmsui.database.entity.BarcodeEntity;
import com.example.gambarucmsui.database.entity.PersonPictureEntity;
import com.example.gambarucmsui.database.entity.TeamEntity;
import com.example.gambarucmsui.database.entity.TeamLogoEntity;
import com.example.gambarucmsui.database.repo.BarcodeRepository;
import com.example.gambarucmsui.database.repo.TeamLogoRepository;
import com.example.gambarucmsui.database.repo.TeamRepository;
import com.example.gambarucmsui.ports.ValidatorResponse;
import com.example.gambarucmsui.ports.interfaces.team.*;
import com.example.gambarucmsui.ui.form.validation.TeamInputValidator;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.example.gambarucmsui.common.Messages.*;
import static com.example.gambarucmsui.util.ImageUtil.resizeAndOptimizeImage;

public class TeamService implements TeamLoadPort, TeamSavePort, TeamUpdatePort, TeamIfExists, TeamPurgePort, TeamDeletePort {

    private final TeamRepository teamRepository;
    private final TeamInputValidator teamValidator = new TeamInputValidator();
    private final BarcodeRepository barcodeRepository;
    private final TeamLogoRepository teamLogoRepository;

    public TeamService(TeamRepository teamRepository, TeamLogoRepository teamLogoRepository, BarcodeRepository barcodeRepository) {
        this.teamRepository = teamRepository;
        this.barcodeRepository = barcodeRepository;
        this.teamLogoRepository = teamLogoRepository;
    }

    @Override
    public ValidatorResponse verifyAndSaveTeam(String teamName, String fee, byte[] pictureData) throws IOException {
        Map<String, String> errors = new HashMap<>();
        if (!teamValidator.isTeamNameValid(teamName)) {
            errors.put(TeamEntity.TEAM_NAME, TEAM_NAME_NOT_VALID);
        }
        if (!teamValidator.isFeeValid(fee)) {
            errors.put(TeamEntity.MEMBERSHIP_PAYMENT, TEAM_FEE_NOT_VALID);
        }
        if (ifTeamNameExists(teamName)) {
            errors.put(TeamEntity.TEAM_NAME, TEAM_NAME_ALREADY_EXISTS);
        }

        if (!errors.isEmpty()) {
            return new ValidatorResponse(errors);
        }

        save(teamName, new BigDecimal(fee), pictureData);
        return new ValidatorResponse(Messages.TEAM_IS_CREATED(teamName));
    }

    @Override
    public TeamEntity save(String teamName, BigDecimal membershipFee, byte[] pictureData) throws IOException {
        TeamEntity en = new TeamEntity(teamName, TeamEntity.Status.ACTIVE, membershipFee);

        TeamEntity team = teamRepository.save(en);
        if (pictureData != null) {
            teamLogoRepository.saveOrUpdatePictureData(team.getTeamId(), pictureData);
        }

        return team;
    }

    @Override
    public ValidatorResponse verifyAndUpdateTeam(Long teamId, String teamName, String fee, byte[] pictureData) throws IOException {
        Optional<TeamEntity> byId = findById(teamId);
        TeamEntity team = byId.get();

        Map<String, String> errors = new HashMap<>();
        if (!teamValidator.isTeamNameValid(teamName)) {
            errors.put("name", TEAM_NAME_NOT_VALID);
        }
        if (!teamValidator.isFeeValid(fee)) {
            errors.put("membershipPayment", TEAM_FEE_NOT_VALID);
        }

        if (!team.getName().equals(teamName) && ifTeamNameExists(teamName)) {
            errors.put("name", TEAM_NAME_ALREADY_EXISTS);
        }

        if (!errors.isEmpty()) {
            return new ValidatorResponse(errors);
        }

        teamName = teamName.trim();
        fee = fee.trim();
        updateTeam(teamId, teamName, new BigDecimal(fee), pictureData);
        return new ValidatorResponse(Messages.TEAM_IS_UPDATED(teamName));

    }

    @Override
    public TeamEntity updateTeam(Long teamId, String teamName, BigDecimal membershipFee, TeamEntity.Status status, byte[] pictureData) throws IOException {
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

            if (pictureData != null) {
                teamLogoRepository.saveOrUpdatePictureData(teamId, pictureData);
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

    @Override
    public ValidatorResponse validateAndDeleteTeam(Long teamId) {
        Map<String, String> errors = new HashMap<>();

        Optional<TeamEntity> byId = teamRepository.findById(teamId);
        if (byId.isEmpty()) {
            errors.put(TeamEntity.TEAM_ID, TEAM_DOESNT_EXIST);
            return new ValidatorResponse(errors);
        }

        List<BarcodeEntity> barcodes = barcodeRepository.findByTeam(teamId);
        for (BarcodeEntity barcode : barcodes) {
            barcode.setStatus(BarcodeEntity.Status.DELETED);
        }
        barcodeRepository.saveAll(barcodes);

        TeamEntity team = byId.get();
        String teamNameBeforeDeletion = team.getName();

        team.setStatus(TeamEntity.Status.DELETED);
        team.setName(team.getName() + "(Obrisan)");
        teamRepository.update(team);

        return new ValidatorResponse(TEAM_IS_DELETED(teamNameBeforeDeletion));
    }
}

