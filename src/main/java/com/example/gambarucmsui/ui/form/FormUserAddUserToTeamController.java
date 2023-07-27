package com.example.gambarucmsui.ui.form;

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
import java.util.ResourceBundle;

public class FormUserAddUserToTeamController implements Initializable {
    private final TeamRepository teamRepository;
    @FXML private VBox root;
    @FXML private Label lblErrUserBarcodeId;
    @FXML private Label lblErrUserTeamName;
    @FXML private ComboBox<?> cmbUserTeamName;
    @FXML private TextField txtUserBarcodeId;

    public FormUserAddUserToTeamController(TeamRepository teamRepository) {
        this.teamRepository = teamRepository;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    @FXML
    void onAddUserToTeam(MouseEvent event) {

    }

    @FXML
    void onClose(MouseEvent event) {

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
    }
}

