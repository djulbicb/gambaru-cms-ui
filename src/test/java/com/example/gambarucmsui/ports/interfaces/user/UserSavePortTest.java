package com.example.gambarucmsui.ports.interfaces.user;

import com.example.gambarucmsui.H2DatabaseConfig;
import com.example.gambarucmsui.common.Messages;
import com.example.gambarucmsui.database.entity.PersonEntity;
import com.example.gambarucmsui.ports.ValidatorResponse;
import javafx.scene.image.ImageView;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static com.example.gambarucmsui.TestData.createBlackImageByteArray;
import static org.junit.jupiter.api.Assertions.*;

class UserSavePortTest extends H2DatabaseConfig {

    @Test
    public void shouldVerifyAndSaveUserWithAndWithoutPicture() throws IOException {
        // given
        PersonEntity savedWithImage = userSavePort.save("Bo", "Lowe", PersonEntity.Gender.MALE, "123", createBlackImageByteArray());
        PersonEntity savedWithoutImage = userSavePort.save("Bo", "Lowe", PersonEntity.Gender.MALE, "123", null);

        // when
        PersonEntity loadedPersonWithImage = userLoadPort.loadUserByUserId(savedWithImage.getPersonId()).get();
        PersonEntity loadedPersonWithoutImage = userLoadPort.loadUserByUserId(savedWithoutImage.getPersonId()).get();

        // then
        assertEquals(savedWithImage, loadedPersonWithImage);
        assertEquals(savedWithoutImage, loadedPersonWithoutImage);

        // Check loading image. When user doesnt have pic, default is loaded
        ImageView loadedImageWithImage = userPictureLoadPort.loadUserPictureByUserId(savedWithImage.getPersonId());
        ImageView loadedImageWithoutImage = userPictureLoadPort.loadUserPictureByUserId(loadedPersonWithoutImage.getPersonId());
        assertNotNull(loadedImageWithImage);
        assertNotNull(loadedImageWithoutImage);
    }

    @Test
    public void shouldFailVerificatio() throws IOException {
        ValidatorResponse resEmpty = userSavePort.verify("", "", "", "");
        ValidatorResponse resBlank = userSavePort.verify(" ", " ", " ", " ");
        ValidatorResponse resNull = userSavePort.verify(null, null, null, null);

        // Verification fails when empty, blank or null
        List<ValidatorResponse> responses = List.of(resEmpty, resBlank, resNull);
        for (ValidatorResponse res : responses) {
            assertEquals(Messages.USER_FIRST_NAME_MISSING, res.getErrorOrEmpty(PersonEntity.FIRST_NAME));
            assertEquals(Messages.USER_LAST_NAME_MISSING, res.getErrorOrEmpty(PersonEntity.LAST_NAME));
            assertEquals(Messages.USER_GENDER_MISSING, res.getErrorOrEmpty(PersonEntity.GENDER));
        }

        // Check gender and phone
        ValidatorResponse resBadPhone = userSavePort.verify("abc", "bcd", "WHATEVER", "WRONG");
        assertEquals(Messages.USER_GENDER_MISSING, resBadPhone.getErrorOrEmpty(PersonEntity.GENDER));
        assertEquals(Messages.USER_PHONE_MISSING, resBadPhone.getErrorOrEmpty(PersonEntity.PHONE));
    }

}