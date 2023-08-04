package com.example.gambarucmsui.ui.form;

import com.example.gambarucmsui.database.entity.UserEntity;
import com.example.gambarucmsui.database.repo.TeamRepository;
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
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.nio.file.Files;
import java.util.ResourceBundle;

import static com.example.gambarucmsui.util.LayoutUtil.getOr;

public class FormUserAddController implements Initializable {
    // Input
    //////////////////////////////////////////
    private final TeamRepository teamRepo;
    private final UserInputValidator validator = new UserInputValidator();

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

    public FormUserAddController(TeamRepository teamRepo) {
        this.teamRepo = teamRepo;
    }

    // OUTPUT DATA
    /////////////////////////////////////////
    private boolean isFormReady;
    private String outFirstName;
    private String outLastName;
    private String outPhone;
    private byte[] outPictureData;
    private UserEntity.Gender outGender;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    @FXML
    void onClose(MouseEvent event) {
        close();
    }

    @FXML
    void onSave(MouseEvent event) {
        String firstNameStr = getOr(txtUserFirstName, "");
        String lastNameStr = getOr(txtUserLastName, "");
        String phoneStr = getOr(txtUserPhone, "");
        String genderStr = getOr(cmbUserGender, "");

        if (validate(firstNameStr, lastNameStr, phoneStr, genderStr)) {
            isFormReady = true;
            outFirstName = firstNameStr.trim();
            outLastName = lastNameStr.trim();
            outPhone = phoneStr.trim();
            outGender = genderStr.equals("Muški") ? UserEntity.Gender.MALE : UserEntity.Gender.FEMALE;
            close();
        }
    }

    boolean validate(String firstNameStr, String lastNameStr, String phoneStr, String genderStr) {
        boolean isFormCorrect = true;

        if (!validator.isValidFirstName(firstNameStr)) {
            lblErrUserFirstName.setText(validator.errFirstName());
            isFormCorrect = false;
        }
        if (!validator.isValidLastName(lastNameStr)) {
            lblErrUserLastName.setText(validator.errLastName());
            isFormCorrect = false;
        }
        if (!validator.isValidPhone(phoneStr)) {
            lblErrUserPhone.setText(validator.errPhone());
            isFormCorrect = false;
        }
        if (!validator.isValidGender(genderStr)) {
            lblErrUserGender.setText(validator.errGender());
            isFormCorrect = false;
        }

        return isFormCorrect;
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

    public boolean isFormReady() {
        return isFormReady;
    }

    public FormUserAddController.Data getData() {
        return new Data(outFirstName, outLastName, outPhone, outGender, outPictureData);
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

    public static class Data {
        private String firstName;
        private String lastName;
        private String phone;
        private UserEntity.Gender gender;
        private byte[] pictureData;

        public Data(String firstName, String lastName, String phone, UserEntity.Gender gender, byte[] pictureData) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.phone = phone;
            this.gender = gender;
            this.pictureData = pictureData;
        }

        public String getFirstName() {
            return firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public String getPhone() {
            return phone;
        }

        public UserEntity.Gender getGender() {
            return gender;
        }

        public byte[] getPictureData() {
            return pictureData;
        }
    }
}
