package com.example.gambarucmsui.ui.form;

import com.example.gambarucmsui.adapter.out.persistence.entity.TeamEntity;
import com.example.gambarucmsui.adapter.out.persistence.entity.UserEntity;
import com.example.gambarucmsui.adapter.out.persistence.repo.TeamRepository;
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
import java.util.List;
import java.util.ResourceBundle;

public class FormUserAddController implements Initializable {
    // Input
    //////////////////////////////////////////
    private final TeamRepository teamRepo;

    // FXML
    //////////////////////////////////////////
    @FXML private ComboBox<String> cmbUserGender;
    @FXML private ComboBox<String> cmbUserTeamName;
    @FXML private Label lblErrUserBarcodeId;
    @FXML private Label lblErrUserFirstName;
    @FXML private Label lblErrUserLastName;
    @FXML private Label lblErrUserPhone;
    @FXML private Label lblErrUserTeamName;
    @FXML private Label lblErrUserGender;
    @FXML private VBox root;
    @FXML private TextField txtUserBarcodeId;
    @FXML private TextField txtUserFirstName;
    @FXML private TextField txtUserLastName;
    @FXML private TextField txtUserPhone;

    public FormUserAddController(TeamRepository teamRepo) {
        this.teamRepo = teamRepo;
    }

    // FORM DATA
    /////////////////////////////////////////
    private boolean isFormReady;
    private Long barcodeId;
    private String firstName;
    private String lastName;
    private String phone;
    private UserEntity.Gender gender;
    private String teamName;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        List<TeamEntity> allTeams = teamRepo.findAll();
        for (TeamEntity team : allTeams) {
            cmbUserTeamName.getItems().add(team.getName());
        }

    }

    public void onBarcodeScanned(String scannedBarcode) {
        txtUserBarcodeId.setText(scannedBarcode.toString());
        lblErrUserBarcodeId.setText("");
    }

    @FXML
    void onClose(MouseEvent event) {
        close();
    }

    @FXML
    void onSave(MouseEvent event) {
        boolean isFormCorrect = true;

        String barcodeIdStr = txtUserBarcodeId.getText().trim();
        String firstNameStr = txtUserFirstName.getText().trim();
        String lastNameStr = txtUserLastName.getText().trim();
        String phoneStr = txtUserPhone.getText().trim();
        String genderStr = cmbUserGender.getSelectionModel().getSelectedItem();
        String teamStr = cmbUserTeamName.getSelectionModel().getSelectedItem();

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
        if (barcodeIdStr.isBlank()) {
            lblErrUserBarcodeId.setText("Skeniraj barkod da bi ga asocirao sa timom.");
            isFormCorrect = false;
        }
        if (teamStr == null || teamStr.isBlank()) {
            lblErrUserTeamName.setText("Izaberi tim.");
            isFormCorrect = false;
        }

        if (!isFormCorrect) {
            return;
        }

        isFormReady = true;
        teamName = teamStr.trim();
        firstName = firstNameStr.trim();
        lastName = lastNameStr.trim();
        phone = phoneStr.trim();
        gender = genderStr.equals("Muški") ? UserEntity.Gender.MALE : UserEntity.Gender.FEMALE;
        teamName = teamStr.trim();
        barcodeId = Long.valueOf(barcodeIdStr);

        close();
    }

    private void close() {
        root.getScene().getWindow().hide();
    }

    public boolean isFormReady() {
        return isFormReady;
    }

    public FormUserAddController.Data getData() {
        return new Data(barcodeId, firstName, lastName, phone, gender, teamName);
    }

    @FXML
    void onCmbGenderAction(ActionEvent event) {
        lblErrUserGender.setText("");
    }

    @FXML
    void onCmbTeamAction(ActionEvent event) {
        lblErrUserTeamName.setText("");
    }

    @FXML
    void txtUserBarcodeIdReset(KeyEvent event) {
        lblErrUserBarcodeId.setText("");
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
        private Long barcodeId;
        private String firstName;
        private String lastName;
        private String phone;
        private UserEntity.Gender gender;
        private String teamName;

        public Data(Long barcodeId, String firstName, String lastName, String phone, UserEntity.Gender gender, String teamName) {
            this.barcodeId = barcodeId;
            this.firstName = firstName;
            this.lastName = lastName;
            this.phone = phone;
            this.gender = gender;
            this.teamName = teamName;
        }

        public Long getBarcodeId() {
            return barcodeId;
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

        public String getTeamName() {
            return teamName;
        }
    }
}
