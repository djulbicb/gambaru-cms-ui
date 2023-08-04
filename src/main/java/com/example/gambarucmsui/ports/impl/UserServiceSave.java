package com.example.gambarucmsui.ports.impl;

import com.example.gambarucmsui.database.entity.BarcodeEntity;
import com.example.gambarucmsui.database.entity.TeamEntity;
import com.example.gambarucmsui.database.entity.UserEntity;
import com.example.gambarucmsui.database.entity.UserPictureEntity;
import com.example.gambarucmsui.database.repo.BarcodeRepository;
import com.example.gambarucmsui.database.repo.TeamRepository;
import com.example.gambarucmsui.database.repo.UserPictureRepository;
import com.example.gambarucmsui.database.repo.UserRepository;
import com.example.gambarucmsui.ports.user.UserAddToTeamPort;
import com.example.gambarucmsui.ports.user.UserSavePort;
import com.example.gambarucmsui.ports.user.UserUpdatePort;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserServiceSave implements UserSavePort, UserUpdatePort, UserAddToTeamPort {
    private final UserRepository userRepo;
    private final UserPictureRepository userPictureRepo;
    private final BarcodeRepository barcodeRepo;
    private final TeamRepository teamRepo;

    public UserServiceSave(BarcodeRepository barcodeRepo, TeamRepository teamRepo, UserRepository userRepo, UserPictureRepository userPictureRepo) {
        this.userRepo = userRepo;
        this.barcodeRepo = barcodeRepo;
        this.teamRepo = teamRepo;
        this.userPictureRepo = userPictureRepo;
    }

    @Override
    public UserEntity save (String firstName, String lastName, UserEntity.Gender gender, String phone, LocalDateTime now, byte[] pictureData) {
        UserEntity user = userRepo.save(new UserEntity(firstName, lastName, gender, phone, now));
        if (pictureData != null) {
            UserPictureEntity picture = userPictureRepo.save(new UserPictureEntity(pictureData, user));
            user.setPicture(picture);
        }
        return user;
    }

    private List<UserEntity> userEntities = new ArrayList<>();
    @Override
    public void addToBulkSave(String firstName, String lastName, UserEntity.Gender gender, String phone, LocalDateTime now, byte[] pictureData) {
        userEntities.add(new UserEntity(firstName, lastName, gender, phone, now));
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
}
