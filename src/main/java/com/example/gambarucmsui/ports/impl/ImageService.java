package com.example.gambarucmsui.ports.impl;

import com.example.gambarucmsui.database.repo.PersonPictureRepository;
import com.example.gambarucmsui.database.repo.UserRepo;
import com.example.gambarucmsui.ports.interfaces.user.PersonPictureBarcodePurgePort;
import com.example.gambarucmsui.ports.interfaces.user.UserPictureLoad;
import com.example.gambarucmsui.util.DataUtil;
import com.example.gambarucmsui.util.PathUtil;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.ByteArrayInputStream;
import java.util.Optional;

public class ImageService implements PersonPictureBarcodePurgePort, UserPictureLoad {
    private final PersonPictureRepository personPictureRepo;
    private final UserRepo userService;

    public ImageService(PersonPictureRepository personPictureRepo, UserRepo personRepo) {
        this.personPictureRepo = personPictureRepo;
        this.userService = personRepo;
    }

    @Override
    public void purge() {
        personPictureRepo.deleteAll();
    }

    @Override
    public ImageView loadUserPictureByUserId(Long userId) {

        Optional<byte[]> picOpt = personPictureRepo.findByPersonId(userId);

        Image userPicture;
        if (picOpt.isEmpty()) {
            userPicture = DataUtil.loadImageFromResources(PathUtil.USER_NOT_FOUND);
        } else {
            userPicture = new Image(new ByteArrayInputStream(picOpt.get()));
        }

        ImageView imageView = new ImageView(userPicture);
        imageView.setFitHeight(400);
        imageView.setFitWidth(300);

        return imageView;
    }
}

