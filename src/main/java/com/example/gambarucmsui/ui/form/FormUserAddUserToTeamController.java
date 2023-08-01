package com.example.gambarucmsui.ui.form;

import com.example.gambarucmsui.database.entity.BarcodeEntity;
import com.example.gambarucmsui.database.entity.TeamEntity;
import com.example.gambarucmsui.database.entity.UserEntity;
import com.example.gambarucmsui.database.repo.BarcodeRepository;
import com.example.gambarucmsui.database.repo.TeamRepository;
import com.example.gambarucmsui.database.repo.UserRepository;
import com.example.gambarucmsui.ui.ToastView;
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
import java.util.Optional;
import java.util.ResourceBundle;

import static com.example.gambarucmsui.util.FormatUtil.isBarcode;
import static com.example.gambarucmsui.util.FormatUtil.parseBarcodeStr;

public class FormUserAddUserToTeamController implements Initializable {
    private final TeamRepository teamRepository;
    private final BarcodeRepository barcodeRepository;
    private final UserRepository userRepo;
    private final Data input;

    @FXML private VBox root;
    @FXML private Label lblErrUserBarcodeId;
    @FXML private Label lblErrUserTeamName;
    @FXML private ComboBox<String> cmbUserTeamName;
    @FXML private TextField txtUserBarcodeId;

    // OUTPUT DATA
    /////////////////////////////////////////
    private boolean isFormReady;
    private Long outBarcodeId;
    private String outTeamName;

    public FormUserAddUserToTeamController(Data input, TeamRepository teamRepository, BarcodeRepository barcodeRepository, UserRepository userRepo) {
        this.input = input;
        this.teamRepository = teamRepository;
        this.barcodeRepository = barcodeRepository;
        this.userRepo = userRepo;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        List<TeamEntity> allTeams = teamRepository.findAllActive();
        for (TeamEntity team : allTeams) {
            cmbUserTeamName.getItems().add(team.getName());
        }
    }

    @FXML
    void onAddUserToTeam(MouseEvent event) {

        String barcodeIdStr = txtUserBarcodeId.getText().trim();
        String teamNameStr = cmbUserTeamName.getSelectionModel().getSelectedItem();

        if (validate(barcodeIdStr, teamNameStr)) {
            isFormReady = true;
            outBarcodeId = parseBarcodeStr(barcodeIdStr);
            outTeamName = teamNameStr;
            close();
        }
    }

    private boolean validate(String barcodeIdStr, String teamNameStr) {
        boolean isFormCorrect = true;

        if (!isBarcode(barcodeIdStr)) {
            lblErrUserBarcodeId.setText("Skeniraj barkod.");
            isFormCorrect = false;
        }
        if (teamNameStr == null || teamNameStr.isBlank()) {
            lblErrUserTeamName.setText("Izaberi tim.");
            isFormCorrect = false;
        }
        if (!isFormCorrect) {
            return false;
        }

        Long barcodeId = parseBarcodeStr(barcodeIdStr);
        Optional<BarcodeEntity> barcodeOpt = barcodeRepository.findById(barcodeId);
        if (barcodeOpt.isEmpty()) {
            lblErrUserBarcodeId.setText("Taj barkod ne postoji u sistemu. Kreiraj ga prvo.");
            isFormCorrect = false;
            return false;
        }
        BarcodeEntity barcode = barcodeOpt.get();
        if (barcode.getStatus() != BarcodeEntity.Status.NOT_USED) {
            lblErrUserBarcodeId.setText("Taj barkod postoji, ali je već u upotrebi. Koristi drugi.");
            isFormCorrect = false;
            return false;
        }

        TeamEntity team = teamRepository.findByName(teamNameStr);
        if (userRepo.isUserAlreadyInThisTeam(input.userId, team.getTeamId())) {
            lblErrUserTeamName.setText("Selektovani polaznik je već u tom timu.");
            isFormCorrect = false;
            return false;
        }
        return true;
    }

    public FormUserAddUserToTeamController.Data getData() {
        return new FormUserAddUserToTeamController.Data(input.getUserId(), outBarcodeId, outTeamName);
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
        lblErrUserBarcodeId.setText("");
    }

    private void close() {
        root.getScene().getWindow().hide();
    }

    public boolean isFormReady() {
        return isFormReady;
    }

    public static class Data {
        private Long userId;
        private Long barcode;
        private String teamName;

        public Data(Long userId, Long barcode, String teamName) {
            this.barcode = barcode;
            this.teamName = teamName;
            this.userId = userId;
        }

        public Long getUserId() {
            return userId;
        }

        public Long getBarcode() {
            return barcode;
        }

        public String getTeamName() {
            return teamName;
        }
    }
}

