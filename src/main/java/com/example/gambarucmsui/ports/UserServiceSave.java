package com.example.gambarucmsui.ports;

import com.example.gambarucmsui.database.entity.UserEntity;
import com.example.gambarucmsui.database.entity.UserPictureEntity;
import com.example.gambarucmsui.database.repo.UserPictureRepository;
import com.example.gambarucmsui.database.repo.UserRepository;
import com.example.gambarucmsui.ports.user.UserSavePort;
import com.example.gambarucmsui.ports.user.UserUpdatePort;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserServiceSave implements UserSavePort, UserUpdatePort {
    private final UserRepository userRepository;
    private final UserPictureRepository userPictureRepository;

    public UserServiceSave(UserRepository userRepository, UserPictureRepository userPictureRepository) {
        this.userRepository = userRepository;
        this.userPictureRepository = userPictureRepository;
    }

    @Override
    public UserEntity save (String firstName, String lastName, UserEntity.Gender gender, String phone, LocalDateTime now, byte[] pictureData) {
        UserEntity user = userRepository.save(new UserEntity(firstName, lastName, gender, phone, now));
        if (pictureData != null) {
            UserPictureEntity picture = userPictureRepository.save(new UserPictureEntity(pictureData, user));
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
        userRepository.saveAll(userEntities);
        userEntities.clear();
    }

    @Override
    public boolean update(Long userId, String firstName, String lastName, UserEntity.Gender gender, String phone, byte[] pictureData) {
        Optional<UserEntity> byId = userRepository.findById(userId);
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
}
