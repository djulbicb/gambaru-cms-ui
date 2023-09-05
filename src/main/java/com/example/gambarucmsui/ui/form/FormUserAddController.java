package com.example.gambarucmsui.ui.form;

import com.example.gambarucmsui.database.entity.PersonEntity;
import com.example.gambarucmsui.ports.Container;
import com.example.gambarucmsui.ports.ValidatorResponse;
import com.example.gambarucmsui.ports.interfaces.user.UserSavePort;
import com.example.gambarucmsui.ui.form.validation.UserInputValidator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.ResourceBundle;

import static com.example.gambarucmsui.util.LayoutUtil.getOr;

public class FormUserAddController implements Initializable {
    // Input
    //////////////////////////////////////////
    protected UserInputValidator validator = new UserInputValidator();
    protected UserSavePort userSavePort;

    // FXML
    //////////////////////////////////////////
    @FXML protected VBox root;
    @FXML protected Button btnSave;
    @FXML protected Button btnClose;
    @FXML protected Label lblErrUserFirstName;
    @FXML protected Label lblErrUserLastName;
    @FXML protected Label lblErrUserPhone;
    @FXML protected Label lblErrUserGender;
    @FXML protected Label lblErrPicture;
    @FXML protected ComboBox<String> cmbUserGender;
    @FXML protected TextField txtUserFirstName;
    @FXML protected TextField txtUserLastName;
    @FXML protected TextField txtUserPhone;

    public FormUserAddController() {
        this.userSavePort = Container.getBean(UserSavePort.class);
    }

    // OUTPUT DATA
    /////////////////////////////////////////
    private byte[] outPictureData;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    @FXML
    void onClose(MouseEvent event) {
        close();
    }

    @FXML
    void onSave(MouseEvent event) throws IOException {
        String firstNameStr = getOr(txtUserFirstName, "");
        String lastNameStr = getOr(txtUserLastName, "");
        String phoneStr = getOr(txtUserPhone, "");
        String genderStr = getOr(cmbUserGender, "");

        if (validate(firstNameStr, lastNameStr, phoneStr, genderStr)) {
            btnSave.setDisable(true);
            PersonEntity.Gender gender = genderStr.equals("Muški") ? PersonEntity.Gender.MALE : PersonEntity.Gender.FEMALE;
            userSavePort.save(firstNameStr, lastNameStr, gender, phoneStr, outPictureData);
            close();
        }
    }


    boolean validate(String firstNameStr, String lastNameStr, String phoneStr, String genderStr) {
        ValidatorResponse verify = userSavePort.verify(firstNameStr, lastNameStr, genderStr, phoneStr);
        if (verify.hasErrors()) {
            lblErrUserFirstName.setText(verify.getErrorOrEmpty("firstName"));
            lblErrUserLastName.setText(verify.getErrorOrEmpty("lastName"));
            lblErrUserPhone.setText(verify.getErrorOrEmpty("phone"));
            lblErrUserGender.setText(verify.getErrorOrEmpty("gender"));
            return false;
        }

        return true;
    }

    @FXML
    private void onAddPicture() throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Stack Trace");
        File file = fileChooser.showOpenDialog(null);
        if (file != null && file.exists()) {
            lblErrPicture.setText("");
            boolean isImage = file.getName().endsWith(".jpg") || file.getName().endsWith(".png") || file.getName().endsWith(".jpeg");
            if (!isImage) {
                lblErrPicture.setText("Mogu samo jpg i png slike.");
                return;
            }
            outPictureData = Files.readAllBytes(file.toPath());
        }

    }

    private void close() {
        root.getScene().getWindow().hide();
    }

    @FXML
    void onCmbGenderAction(ActionEvent event) {
        lblErrUserGender.setText("");
    }

    @FXML
    void txtUserFirstNameReset(KeyEvent event) {
        lblErrUserFirstName.setText("");
    }

    @FXML
    void txtUserLastNameReset(KeyEvent event) {
        lblErrUserLastName.setText("");
    }

    @FXML
    void txtUserPhoneReset(KeyEvent event) {
        lblErrUserPhone.setText("");
    }
}
