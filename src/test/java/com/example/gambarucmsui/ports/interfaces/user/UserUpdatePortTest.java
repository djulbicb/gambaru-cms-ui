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

class UserUpdatePortTest extends H2DatabaseConfig {
    @Test
    public void shouldVerifyAndUpdateUserWithAndWithoutPicture() throws IOException {
        // given
        PersonEntity en = userSavePort.save("Bo", "Lowe", PersonEntity.Gender.MALE, "123", createBlackImageByteArray());

        // when
        userUpdatePort.update(en.getPersonId(), "Bo1", "Lowe1", PersonEntity.Gender.FEMALE, "234", createBlackImageByteArray());

        // then
        PersonEntity loadedPersonWithImage = userLoadPort.loadUserByUserId(en.getPersonId()).get();
        assertEquals(en.getFirstName(), "Bo1");
        assertEquals(en.getLastName(), "Lowe1");
        assertEquals(en.getGender(), PersonEntity.Gender.FEMALE);
        assertEquals(en.getPhone(), "234");

        // Check loading image. When user doesnt have pic, default is loaded
        ImageView loadedImageWithoutImage = userPictureLoadPort.loadUserPictureByUserId(en.getPersonId());
        assertNotNull(loadedImageWithoutImage);
    }

    @Test
    public void shouldFailVerificatio() {
        ValidatorResponse resEmpty = userUpdatePort.verify("", "", "", "");
        ValidatorResponse resBlank = userUpdatePort.verify(" ", " ", " ", " ");
        ValidatorResponse resNull = userUpdatePort.verify(null, null, null, null);

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