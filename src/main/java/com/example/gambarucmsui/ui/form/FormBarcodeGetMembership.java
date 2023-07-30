package com.example.gambarucmsui.ui.form;

import com.example.gambarucmsui.adapter.out.persistence.entity.BarcodeEntity;
import com.example.gambarucmsui.adapter.out.persistence.entity.UserEntity;
import com.example.gambarucmsui.adapter.out.persistence.repo.BarcodeRepository;
import com.example.gambarucmsui.adapter.out.persistence.repo.TeamRepository;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

import java.util.Optional;

import static com.example.gambarucmsui.util.FormatUtil.*;

public class FormBarcodeGetMembership {

    // REPO
    //////////////////////////////////////////
    private final BarcodeRepository barcodeRepo;
    private final TeamRepository teamRepo;

    // FXML
    //////////////////////////////////////////
    @FXML private VBox root;
    @FXML private Label lblResult;
    @FXML private Label lblErrBarcodeId;
    @FXML private Label lblErrMembershipFee;
    @FXML private Label lblErrTeam;
    @FXML private TextField txtBarcodeId;
    @FXML private TextField txtMembershipFee;
    @FXML private TextField txtTeam;


    // OUTPUT DATA
    /////////////////////////////////////////
    private boolean isFormReady = false;
    private Long outBarcodeId;

    public FormBarcodeGetMembership(BarcodeRepository barcodeRepo, TeamRepository teamRepo) {
        this.barcodeRepo = barcodeRepo;
        this.teamRepo = teamRepo;
    }

    @FXML void onOk(MouseEvent event) {
        if (!isFormReady) {
            lblErrBarcodeId.setText("Skeniraj dobar barkod.");
            return;
        }
        String barcodeId = txtBarcodeId.getText().trim();
        outBarcodeId = parseBarcodeStr(barcodeId);
        close();
    }

    @FXML void onClose(MouseEvent event) {
        close();
    }
    @FXML
    void onBarcodeIdTyped(KeyEvent event) {
        isFormReady = false;
        lblErrBarcodeId.setText("");
        lblResult.setText("");
        txtTeam.setText("");
        txtMembershipFee.setText("");

        String barcodeIdStr = txtBarcodeId.getText();
        if (!isLong(barcodeIdStr)) {
            if (!barcodeIdStr.isBlank()) {
                lblErrBarcodeId.setText("Upiši barkod npr 123.");
            }
            return;
        }

        Long barcode = parseBarcodeStr(barcodeIdStr);
        Optional<BarcodeEntity> barcodeEntityOptional = barcodeRepo.findById(barcode);
        if (barcodeEntityOptional.isEmpty()) {
            lblErrBarcodeId.setText("Taj barkod ne postoji u bazi.");
            return;
        }

        BarcodeEntity b = barcodeEntityOptional.get();
        if (b.getStatus() != BarcodeEntity.Status.ASSIGNED)  {
            lblErrBarcodeId.setText("Taj barkod postoji ali nije u upotrebi. Probaj drugi drugi.");
            return;
        }

        UserEntity user = b.getUser();
        lblResult.setText(String.format("Korisnik nađen: %s %s.", user.getFirstName(), user.getLastName()));

        txtTeam.setText(b.getTeam().getName());
        txtMembershipFee.setText(b.getTeam().getMembershipPayment().toString());

        isFormReady = true;
    }

    public boolean isFormReady() {
        return isFormReady;
    }

    public Long getBarcodeId() {
        return outBarcodeId;
    }

    private void close() {
        root.getScene().getWindow().hide();
    }

}
