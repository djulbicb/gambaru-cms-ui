package com.example.gambarucmsui.ports.interfaces.user;

import javafx.scene.image.ImageView;

public interface UserPictureLoad {
    default ImageView loadUserPictureByUserId(Long userId) {
        return loadUserPictureByUserId(userId, 400, 300);
    };
    ImageView loadUserPictureByUserId(Long userId, int height, int width);
}
