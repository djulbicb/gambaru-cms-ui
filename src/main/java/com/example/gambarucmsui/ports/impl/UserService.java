package com.example.gambarucmsui.ports.impl;

import com.example.gambarucmsui.common.Messages;
import com.example.gambarucmsui.database.entity.*;
import com.example.gambarucmsui.database.repo.*;
import com.example.gambarucmsui.ports.ValidatorResponse;
import com.example.gambarucmsui.ports.interfaces.user.*;
import com.example.gambarucmsui.ui.form.validation.TeamInputValidator;
import com.example.gambarucmsui.ui.form.validation.UserInputValidator;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static com.example.gambarucmsui.database.entity.BarcodeEntity.BARCODE_ID;
import static com.example.gambarucmsui.util.FormatUtil.*;
import static com.example.gambarucmsui.util.ImageUtil.resizeAndOptimizeImage;

public class UserService implements UserSavePort, UserUpdatePort, UserLoadPort, UserAddToTeamPort, IsUserAlreadyInThisTeamPort, UserPurgePort {
    private final UserRepo userRepo;
    private final UserPictureRepository userPictureRepo;
    private final BarcodeRepository barcodeRepo;
    private final TeamRepository teamRepo;
    private final UserInputValidator userVal = new UserInputValidator();
    private final TeamInputValidator teamVal = new TeamInputValidator();
    private final UserAttendanceRepository userAttendanceRepo;
    private final SubscriptionService subscriptionService;

    public UserService(
            BarcodeRepository barcodeRepo,
            TeamRepository teamRepo,
            UserRepo userRepo,
            UserAttendanceRepository userAttendanceRepo,
            UserPictureRepository userPictureRepo,
            SubscriptionService subscriptionService) {
        this.userRepo = userRepo;
        this.barcodeRepo = barcodeRepo;
        this.teamRepo = teamRepo;
        this.userPictureRepo = userPictureRepo;
        this.userAttendanceRepo = userAttendanceRepo;
        this.subscriptionService = subscriptionService;
    }

    @Override
    public ValidatorResponse verify(String firstName, String lastName, String gender, String phone) {
        Map<String, String> errors = new HashMap<>();
        if (!userVal.isValidFirstName(firstName)) {
            errors.put(PersonEntity.FIRST_NAME, Messages.USER_FIRST_NAME_MISSING);
        }
        if (!userVal.isValidLastName(lastName)) {
            errors.put(PersonEntity.LAST_NAME, Messages.USER_LAST_NAME_MISSING);
        }
        if (!userVal.isValidPhone(phone)) {
            errors.put(PersonEntity.PHONE, Messages.USER_PHONE_MISSING);
        }
        if (!userVal.isValidGender(gender)) {
            errors.put(PersonEntity.GENDER, Messages.USER_GENDER_MISSING);
        }
        return new ValidatorResponse(errors);
    }

    @Override
    public PersonEntity save(String firstName, String lastName, PersonEntity.Gender gender, String phone, byte[] pictureData) throws IOException {
        Map<String, String > errors = new HashMap<>();
        PersonEntity user = userRepo.save(new PersonEntity(firstName, lastName, gender, phone, LocalDateTime.now()));
        if (pictureData != null) {
            ByteArrayInputStream byteArrayInputStream = resizeAndOptimizeImage(pictureData, 400);
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
                ByteArrayInputStream byteArrayInputStream = resizeAndOptimizeImage(pictureData, 400);

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


    protected void addUserToTeam(Long userId, Long barcodeId, Long teamId, boolean isFreeOfCharge, LocalDate start, LocalDate end) {
        Optional<BarcodeEntity> barcodeOpt = barcodeRepo.findById(barcodeId);
        Optional<PersonEntity> userOpt = userRepo.findById(userId);
        Optional<TeamEntity> teamOpt = teamRepo.findById(teamId);

        if (barcodeOpt.isPresent() && userOpt.isPresent() && teamOpt.isPresent()) {
            BarcodeEntity barcode = barcodeOpt.get();
            PersonEntity user = userOpt.get();
            TeamEntity team = teamOpt.get();

            barcode.setStatus(BarcodeEntity.Status.ASSIGNED);
            barcode.setTeam(team);
            barcode.setPerson(user);
            barcodeRepo.update(barcode);

            user.getBarcodes().add(barcode);

            if (isFreeOfCharge) {
                subscriptionService.addFreeSubscription(barcodeId, teamId);
                return;
            }

            Optional<SubscriptionEntity> subscriptionEntity = subscriptionService.addSubscription(barcodeId, teamId, start, end);
            if (subscriptionEntity.isPresent()) {
                barcode.setSubscription(subscriptionEntity.get());
                barcodeRepo.update(barcode);
            }
        }
    }

    @Override
    public ValidatorResponse verifyAddUserToPort(Long userId, String barcodeIdStr, String teamName, boolean isFreeOfCharge, LocalDate start, LocalDate end) {
        Map<String, String> errors = new HashMap<>();

        if (!isFreeOfCharge && start != null && end != null) {
            if (start.isAfter(end)) {
                errors.put(SubscriptionEntity.SUB, "Početak članarine ne može da bude posle kraja članarine.");
            }
        }
        if (!isLong(barcodeIdStr)) {
            errors.put(BARCODE_ID, "Skeniraj barkod.");
        }
        if (!teamVal.isTeamNameValid(teamName)) {
            errors.put(TeamEntity.TEAM_NAME, "Izaberi tim.");
        }

        if (!errors.isEmpty()) {
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
            errors.put("teamName", Messages.TEAM_NAME_NOT_VALID);
            return new ValidatorResponse(errors);
        }

        if (isUserAlreadyInThisTeam(userId, teamName)) {
            errors.put("teamName", "Selektovani polaznik je već u tom timu.");
            return new ValidatorResponse(errors);
        }

        return new ValidatorResponse(errors);
    }

    @Override
    public ValidatorResponse verifyAndAddUserToPort(Long userId, String barcodeId, String teamName, boolean isFreeOfCharge, LocalDate start, LocalDate end) {
        ValidatorResponse res = verifyAddUserToPort(userId, barcodeId, teamName, isFreeOfCharge, start, end);
        if (res.hasErrors()) {
            return res;
        }
        TeamEntity team = teamRepo.findByName(teamName);
        addUserToTeam(userId, parseBarcodeStr(barcodeId), team.getTeamId(), isFreeOfCharge, start, end);
        return new ValidatorResponse(Messages.USER_ADDED_TO_TEAM(teamName));
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
    public List<BarcodeEntity> findActiveUsersByTeamId(Long teamId) {
        return userRepo.findAllActiveUsersInTeam(teamId);
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
