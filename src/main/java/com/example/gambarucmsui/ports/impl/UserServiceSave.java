package com.example.gambarucmsui.ports.impl;

import com.example.gambarucmsui.database.entity.*;
import com.example.gambarucmsui.database.repo.*;
import com.example.gambarucmsui.ports.ValidatorResponse;
import com.example.gambarucmsui.ports.user.*;
import com.example.gambarucmsui.ui.form.validation.TeamInputValidator;
import com.example.gambarucmsui.ui.form.validation.UserInputValidator;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static com.example.gambarucmsui.util.FormatUtil.isBarcodeString;
import static com.example.gambarucmsui.util.FormatUtil.parseBarcodeStr;

public class UserServiceSave implements UserSavePort, UserUpdatePort, UserLoadPort, UserAddToTeamPort, IsUserAlreadyInThisTeamPort {
    private final UserRepository userRepo;
    private final UserPictureRepository userPictureRepo;
    private final BarcodeRepository barcodeRepo;
    private final TeamRepository teamRepo;
    private final UserInputValidator userVal = new UserInputValidator();
    private final TeamInputValidator teamVal = new TeamInputValidator();
    private final UserAttendanceRepository userAttendanceRepo;

    public UserServiceSave(
            BarcodeRepository barcodeRepo,
            TeamRepository teamRepo,
            UserRepository userRepo,
            UserAttendanceRepository userAttendanceRepo,
            UserPictureRepository userPictureRepo) {
        this.userRepo = userRepo;
        this.barcodeRepo = barcodeRepo;
        this.teamRepo = teamRepo;
        this.userPictureRepo = userPictureRepo;
        this.userAttendanceRepo = userAttendanceRepo;
    }

    @Override
    public ValidatorResponse verify(String firstName, String lastName, String gender, String phone) {
        Map<String, String> errors = new HashMap<>();
        if (!userVal.isValidFirstName(firstName)) {
            errors.put("firstName", userVal.errFirstName());
        }
        if (!userVal.isValidLastName(firstName)) {
            errors.put("lastName", userVal.errLastName());
        }
        if (!userVal.isValidPhone(phone)) {
            errors.put("phone", userVal.errPhone());
        }
        if (!userVal.isValidGender(gender)) {
            errors.put("gender", userVal.errGender());
        }
        return new ValidatorResponse(errors);
    }

    @Override
    public UserEntity save (String firstName, String lastName, UserEntity.Gender gender, String phone, byte[] pictureData) {
        UserEntity user = userRepo.save(new UserEntity(firstName, lastName, gender, phone, LocalDateTime.now()));
        if (pictureData != null) {
            UserPictureEntity picture = userPictureRepo.save(new UserPictureEntity(pictureData, user));
            user.setPicture(picture);
        }
        return user;
    }

    private List<UserEntity> userEntities = new ArrayList<>();
    @Override
    public void addToBulkSave(String firstName, String lastName, UserEntity.Gender gender, String phone, byte[] pictureData) {
        userEntities.add(new UserEntity(firstName, lastName, gender, phone, LocalDateTime.now()));
    }

    @Override
    public void executeBulkSave() {
        userRepo.saveAll(userEntities);
        userEntities.clear();
    }

    @Override
    public boolean update(Long userId, String firstName, String lastName, UserEntity.Gender gender, String phone, byte[] pictureData) {
        Optional<UserEntity> byId = userRepo.findById(userId);
        if (byId.isPresent()) {
            UserEntity user = byId.get();
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setGender(gender);
            user.setPhone(phone);

            if (pictureData != null) {
                user.getPicture().setPictureData(pictureData);
            }
            return true;
        }
        return false;
    }

    @Override
    public void addUserToPort(Long userId, Long barcodeId, String teamName) {
        Optional<BarcodeEntity> barcodeOpt = barcodeRepo.findById(barcodeId);
        Optional<UserEntity> userOpt = userRepo.findById(userId);

        if (barcodeOpt.isPresent() && userOpt.isPresent()) {
            BarcodeEntity barcode = barcodeOpt.get();
            UserEntity user = userOpt.get();
            TeamEntity team = teamRepo.findByName(teamName);

            barcodeRepo.updateBarcodeWithUserAndTeam(barcode, user, team);
            user.getBarcodes().add(barcode);
            userRepo.update(user);
        }
    }

    @Override
    public ValidatorResponse verifyAddUserToPort(Long userId, String barcodeIdStr, String teamName) {
        Map<String, String> errors = new HashMap<>();

        if (!isBarcodeString(barcodeIdStr)) {
            errors.put("barcodeId", "Skeniraj barkod.");
            return new ValidatorResponse(errors);
        }

        Long barcodeId = parseBarcodeStr(barcodeIdStr);
        Optional<BarcodeEntity> barcodeOpt = barcodeRepo.findById(barcodeId);

        if (barcodeOpt.isEmpty()) {
            errors.put("barcodeId", "Taj barkod ne postoji u sistemu. Kreiraj ga prvo.");
            return new ValidatorResponse(errors);
        }

        BarcodeEntity barcode = barcodeOpt.get();
        if (barcode.getStatus() != BarcodeEntity.Status.NOT_USED) {
            errors.put("barcodeId", "Taj barkod postoji, ali je već u upotrebi. Koristi drugi.");
            return new ValidatorResponse(errors);
        }

        if (!teamVal.isTeamNameValid(teamName)) {
            errors.put("teamName", teamVal.errTeamName());
            return new ValidatorResponse(errors);
        }

        if (isUserAlreadyInThisTeam(userId, teamName)) {
            errors.put("teamName", "Selektovani polaznik je već u tom timu.");
            return new ValidatorResponse(errors);
        }

        return new ValidatorResponse(errors);
    }

    @Override
    public Optional<UserEntity> loadUserByUserId(Long userId) {
        return userRepo.findById(userId);
    }

    @Override
    public boolean isUserAlreadyInThisTeam(Long userId, Long teamId) {
        return userRepo.isUserAlreadyInThisTeam(userId, teamId);
    }
    @Override
    public boolean isUserAlreadyInThisTeam(Long userId, String teamName) {
        return userRepo.isUserAlreadyInThisTeam(userId, teamName);
    }
}
