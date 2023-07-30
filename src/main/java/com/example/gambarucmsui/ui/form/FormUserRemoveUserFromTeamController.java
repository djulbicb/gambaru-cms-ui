package com.example.gambarucmsui.ui.form;

import com.example.gambarucmsui.adapter.out.persistence.entity.TeamEntity;
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

import static com.example.gambarucmsui.util.FormatUtil.isBarcode;

public class FormUserRemoveUserFromTeamController implements Initializable {
    private final TeamRepository teamRepository;
    @FXML private VBox root;
    @FXML private Label lblErrUserBarcodeId;
    @FXML private Label lblErrUserTeamName;
    @FXML private ComboBox<String> cmbUserTeamName;
    @FXML private TextField txtUserBarcodeId;

    // INPUT DATA
    /////////////////////////////////////////
    private final Data prop;

    // OUTPUT DATA
    /////////////////////////////////////////
    private boolean isFormReady;
    private String outBarcodeId;
    private String outTeamName;

    public FormUserRemoveUserFromTeamController(TeamRepository teamRepository, Data prop) {
        this.teamRepository = teamRepository;
        this.prop = prop;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        List<TeamEntity> allTeams = teamRepository.findAllByUserId(prop.getUserId());
        for (TeamEntity team : allTeams) {
            cmbUserTeamName.getItems().add(team.getName());
        }
    }

    @FXML
    void onRemoveUserFromTeam(MouseEvent event) {
        boolean isFormCorrect = true;

        String barcodeIdStr = txtUserBarcodeId.getText().trim();
        String teamNameStr = cmbUserTeamName.getSelectionModel().getSelectedItem();

        if (!isBarcode(barcodeIdStr)) {
            lblErrUserBarcodeId.setText("Skeniraj barkod.");
            isFormCorrect = false;
        }
        if (teamNameStr == null || teamNameStr.isBlank()) {
            lblErrUserTeamName.setText("Izaberi tim.");
            isFormCorrect = false;
        }
        if (!isFormCorrect) {
            return;
        }

        isFormReady = true;
        outBarcodeId = barcodeIdStr;
        outTeamName = teamNameStr;

        close();
    }

    public FormUserRemoveUserFromTeamController.Data getData() {
        return new FormUserRemoveUserFromTeamController.Data( prop.getUserId(), prop.getFirstName(), prop.getLastName(), outBarcodeId, outTeamName);
    }

    @FXML
    void onClose(MouseEvent event) {
        close();
    }

    @FXML
    void onCmbTeamAction(ActionEvent event) {
        lblErrUserTeamName.setText("");
    }

    @FXML
    void txtUserBarcodeIdReset(KeyEvent event) {
        lblErrUserBarcodeId.setText("");
    }


    public void onBarcodeScanned(String numberOnly) {
        txtUserBarcodeId.setText(numberOnly);
    }

    private void close() {
        root.getScene().getWindow().hide();
    }

    public boolean isFormReady() {
        return isFormReady;
    }

    public static class Data {
        private String firstName;
        private String lastName;
        private String barcode;
        private String teamName;
        private Long userId;

        public Data(Long userId, String firstName, String lastName, String barcode, String teamName) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.barcode = barcode;
            this.teamName = teamName;
            this.userId = userId;
        }

        public String getFirstName() {
            return firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public String getBarcode() {
            return barcode;
        }

        public String getTeamName() {
            return teamName;
        }

        public Long getUserId() {
            return userId;
        }
    }
}

