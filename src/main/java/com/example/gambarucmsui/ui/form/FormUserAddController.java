package com.example.gambarucmsui.ui.form;

import com.example.gambarucmsui.database.entity.PersonEntity;
import com.example.gambarucmsui.ports.Container;
import com.example.gambarucmsui.ports.ValidatorResponse;
import com.example.gambarucmsui.ports.interfaces.user.UserSavePort;
import com.example.gambarucmsui.ui.form.validation.UserInputValidator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
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
import java.util.Map;
import java.util.ResourceBundle;

import static com.example.gambarucmsui.util.LayoutUtil.getOr;

public class FormUserAddController implements Initializable {
    // Input
    //////////////////////////////////////////
    private final UserInputValidator validator = new UserInputValidator();
    private final UserSavePort port;

    // FXML
    //////////////////////////////////////////
    @FXML private VBox root;
    @FXML private Label lblErrUserFirstName;
    @FXML private Label lblErrUserLastName;
    @FXML private Label lblErrUserPhone;
    @FXML private Label lblErrUserGender;
    @FXML private ComboBox<String> cmbUserGender;
    @FXML private TextField txtUserFirstName;
    @FXML private TextField txtUserLastName;
    @FXML private TextField txtUserPhone;

    public FormUserAddController() {
        this.port = Container.getBean(UserSavePort.class);
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
            PersonEntity.Gender gender = genderStr.equals("Muški") ? PersonEntity.Gender.MALE : PersonEntity.Gender.FEMALE;
            port.save(firstNameStr, lastNameStr, gender, phoneStr, outPictureData);
            close();
        }
    }

    boolean validate(String firstNameStr, String lastNameStr, String phoneStr, String genderStr) {
        ValidatorResponse verify = port.verify(firstNameStr, lastNameStr, genderStr, phoneStr);
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
