package com.example.gambarucmsui.ports.interfaces.user;

import com.example.gambarucmsui.H2DatabaseConfig;
import com.example.gambarucmsui.database.entity.PersonEntity;
import javafx.scene.image.ImageView;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static com.example.gambarucmsui.TestData.createBlackImageByteArray;
import static com.example.gambarucmsui.util.ImageUtil.resizeAndOptimizeImage;
import static org.junit.jupiter.api.Assertions.*;

class UserPictureLoadTest extends H2DatabaseConfig {
    @Test
    public void shouldSaveUserPicture() throws IOException {
        PersonEntity savedWithImage = userSavePort.save("Bo", "Lowe", PersonEntity.Gender.MALE, "123", createBlackImageByteArray());
        PersonEntity savedWithoutImage = userSavePort.save("Bo", "Lowe", PersonEntity.Gender.MALE, "123", null);

        ImageView picSaveWithImage = userPictureLoadPort.loadUserPictureByUserId(savedWithImage.getPersonId());
        ImageView picSavedWithoutImage = userPictureLoadPort.loadUserPictureByUserId(savedWithoutImage.getPersonId());

        assertNotNull(picSaveWithImage.getImage());
        assertNotNull(picSavedWithoutImage.getImage());
    }

}