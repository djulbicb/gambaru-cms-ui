package com.example.gambarucmsui.ui.form;

import com.example.gambarucmsui.database.entity.UserEntity;
import com.example.gambarucmsui.database.repo.TeamRepository;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public class FormUserUpdateController implements Initializable {
    // Input
    //////////////////////////////////////////
    private final TeamRepository teamRepo;
    private final Data input;

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

    public FormUserUpdateController(TeamRepository teamRepo, Data input) {
        this.teamRepo = teamRepo;
        this.input = input;
    }

    // OUTPUT DATA
    /////////////////////////////////////////
    private boolean isFormReady;
    private String outFirstName;
    private String outLastName;
    private String outPhone;
    private UserEntity.Gender outGender;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        txtUserFirstName.setText(input.getFirstName());
        txtUserLastName.setText(input.getLastName());
        txtUserPhone.setText(input.getPhone());
        cmbUserGender.getSelectionModel().selectFirst();
    }

    @FXML
    void onClose(MouseEvent event) {
        close();
    }

    @FXML
    void onSave(MouseEvent event) {
        boolean isFormCorrect = true;

        String firstNameStr = txtUserFirstName.getText().trim();
        String lastNameStr = txtUserLastName.getText().trim();
        String phoneStr = txtUserPhone.getText().trim();
        String genderStr = cmbUserGender.getSelectionModel().getSelectedItem();

        if (firstNameStr.isBlank()) {
            lblErrUserFirstName.setText("Upiši ime.");
            isFormCorrect = false;
        }
        if (lastNameStr.isBlank()) {
            lblErrUserLastName.setText("Upiši prezime.");
            isFormCorrect = false;
        }
        if (phoneStr.isBlank()) {
            lblErrUserPhone.setText("Upiši telefon.");
            isFormCorrect = false;
        }
        if (genderStr == null || genderStr.isBlank()) {
            lblErrUserGender.setText("Izaberi pol.");
            isFormCorrect = false;
        }

        if (!isFormCorrect) {
            return;
        }

        isFormReady = true;
        outFirstName = firstNameStr.trim();
        outLastName = lastNameStr.trim();
        outPhone = phoneStr.trim();
        outGender = genderStr.equals("Muški") ? UserEntity.Gender.MALE : UserEntity.Gender.FEMALE;

        close();
    }

    private void close() {
        root.getScene().getWindow().hide();
    }

    public boolean isFormReady() {
        return isFormReady;
    }

    public FormUserUpdateController.Data getData() {
        return new Data(outFirstName, outLastName, outPhone, outGender);
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

        public Data(String firstName, String lastName, String phone, UserEntity.Gender gender) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.phone = phone;
            this.gender = gender;
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
    }
}
