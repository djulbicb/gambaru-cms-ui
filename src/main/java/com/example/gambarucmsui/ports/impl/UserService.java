package com.example.gambarucmsui.ports.impl;

import com.example.gambarucmsui.database.entity.*;
import com.example.gambarucmsui.database.repo.*;
import com.example.gambarucmsui.ports.ValidatorResponse;
import com.example.gambarucmsui.ports.interfaces.user.*;
import com.example.gambarucmsui.ui.form.validation.TeamInputValidator;
import com.example.gambarucmsui.ui.form.validation.UserInputValidator;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

import static com.example.gambarucmsui.database.entity.BarcodeEntity.BARCODE_ID;
import static com.example.gambarucmsui.util.FormatUtil.isBarcodeString;
import static com.example.gambarucmsui.util.FormatUtil.parseBarcodeStr;
import static com.example.gambarucmsui.util.ImageUtil.resizeAndOptimizeImage;

public class UserService implements UserSavePort, UserUpdatePort, UserLoadPort, UserAddToTeamPort, IsUserAlreadyInThisTeamPort, UserPurgePort {
    private final UserRepo userRepo;
    private final UserPictureRepository userPictureRepo;
    private final BarcodeRepository barcodeRepo;
    private final TeamRepository teamRepo;
    private final UserInputValidator userVal = new UserInputValidator();
    private final TeamInputValidator teamVal = new TeamInputValidator();
    private final UserAttendanceRepository userAttendanceRepo;

    public UserService(
            BarcodeRepository barcodeRepo,
            TeamRepository teamRepo,
            UserRepo userRepo,
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
    public PersonEntity save (String firstName, String lastName, PersonEntity.Gender gender, String phone, byte[] pictureData) throws IOException {
        PersonEntity user = userRepo.save(new PersonEntity(firstName, lastName, gender, phone, LocalDateTime.now()));
        if (pictureData != null) {
            ByteArrayInputStream byteArrayInputStream = resizeAndOptimizeImage(pictureData, 300);
            PersonPictureEntity picture = userPictureRepo.save(new PersonPictureEntity(byteArrayInputStream.readAllBytes(), user));
            user.setPicture(picture);
        }
        return user;
    }

    private List<PersonEntity> userEntities = new ArrayList<>();
    @Override
    public void addToBulkSave(String firstName, String lastName, PersonEntity.Gender gender, String phone, byte[] pictureData) {
        userEntities.add(new PersonEntity(firstName, lastName, gender, phone, LocalDateTime.now()));
    }

    @Override
    public void executeBulkSave() {
        userRepo.saveAll(userEntities);
        userEntities.clear();
    }

    @Override
    public boolean update(Long userId, String firstName, String lastName, PersonEntity.Gender gender, String phone, byte[] pictureData) throws IOException {
        Optional<PersonEntity> byId = userRepo.findById(userId);
        if (byId.isPresent()) {
            PersonEntity user = byId.get();
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setGender(gender);
            user.setPhone(phone);

            if (pictureData != null) {
                ByteArrayInputStream byteArrayInputStream = resizeAndOptimizeImage(pictureData, 300);

                if (user.getPicture() == null) {
                    PersonPictureEntity picture = userPictureRepo.save(new PersonPictureEntity(byteArrayInputStream.readAllBytes(), user));
                    user.setPicture(picture);
                } else {
                    PersonPictureEntity picture = user.getPicture();
                    picture.setPictureData(byteArrayInputStream.readAllBytes());
                    userPictureRepo.update(picture);
                }

            }

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
        Optional<PersonEntity> userOpt = userRepo.findById(userId);

        if (barcodeOpt.isPresent() && userOpt.isPresent()) {
            BarcodeEntity barcode = barcodeOpt.get();
            PersonEntity user = userOpt.get();
            TeamEntity team = teamRepo.findByName(teamName);

            barcodeRepo.updateBarcodeWithUserAndTeam(barcode, user, team);
            userRepo.update(user);
            user.getBarcodes().add(barcode);
        }
    }

    @Override
    public ValidatorResponse verifyAddUserToPort(Long userId, String barcodeIdStr, String teamName) {
        Map<String, String> errors = new HashMap<>();

        if (!isBarcodeString(barcodeIdStr)) {
            errors.put(BARCODE_ID, "Skeniraj barkod.");
            return new ValidatorResponse(errors);
        }

        Long barcodeId = parseBarcodeStr(barcodeIdStr);
        Optional<BarcodeEntity> barcodeOpt = barcodeRepo.findById(barcodeId);

        if (barcodeOpt.isEmpty()) {
            errors.put(BARCODE_ID, "Taj barkod ne postoji u sistemu. Kreiraj ga prvo.");
            return new ValidatorResponse(errors);
        }

        BarcodeEntity barcode = barcodeOpt.get();
        if (barcode.getStatus() != BarcodeEntity.Status.NOT_USED) {
            errors.put(BARCODE_ID, "Taj barkod postoji, ali je već u upotrebi. Koristi drugi.");
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
    public Optional<PersonEntity> loadUserByUserId(Long userId) {
        return userRepo.findById(userId);
    }

    @Override
    public Optional<PersonEntity> findUserByBarcodeId(Long barcodeId) {
        return userRepo.findUserByBarcodeId(barcodeId);
    }

    @Override
    public List<PersonEntity> findAll() {
        return userRepo.findAll();
    }

    @Override
    public List<PersonEntity> findAll(int page, int pageSize, String sortColumn, String teamName, String firstName, String lastName, String barcode, boolean isOnlyActive) {
        return userRepo.findAll(page, pageSize, sortColumn, teamName, firstName, lastName, barcode, isOnlyActive);
    }

    @Override
    public List<BarcodeEntity> findUsersByTeamId(Long teamId) {
        return userRepo.findAllUsersInTeam(teamId);
    }


    @Override
    public boolean isUserAlreadyInThisTeam(Long userId, Long teamId) {
        return userRepo.isUserAlreadyInThisTeam(userId, teamId);
    }
    @Override
    public boolean isUserAlreadyInThisTeam(Long userId, String teamName) {
        return userRepo.isUserAlreadyInThisTeam(userId, teamName);
    }

    @Override
    public void purge() {
        userRepo.deleteAll();
    }
}
