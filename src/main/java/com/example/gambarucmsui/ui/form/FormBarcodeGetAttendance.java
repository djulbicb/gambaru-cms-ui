package com.example.gambarucmsui.ui.form;

import com.example.gambarucmsui.database.entity.BarcodeEntity;
import com.example.gambarucmsui.database.entity.UserEntity;
import com.example.gambarucmsui.database.repo.BarcodeRepository;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

import java.util.Optional;

import static com.example.gambarucmsui.util.FormatUtil.*;

public class FormBarcodeGetAttendance {

    // REPO
    //////////////////////////////////////////
    private final BarcodeRepository barcodeRepo;

    // FXML
    //////////////////////////////////////////
    @FXML private VBox root;
    @FXML private TextField txtBarcodeId;
    @FXML private Label lblErrBarcodeId;
    @FXML private Label lblResult;

    // OUTPUT DATA
    /////////////////////////////////////////
    private boolean isFormReady = false;
    private Long outBarcodeId;

    public FormBarcodeGetAttendance(BarcodeRepository barcodeRepo) {
        this.barcodeRepo = barcodeRepo;
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
    void onTextfieldTyped(KeyEvent event) {
        isFormReady = false;
        lblErrBarcodeId.setText("");
        lblResult.setText("");

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
        if (b.getStatus() != BarcodeEntity.Status.ASSIGNED || b.getUser() == null)  {
            lblErrBarcodeId.setText("Taj barkod postoji ali je već u upotrebi. Probaj drugi drugi.");
            return;
        }

        UserEntity user = b.getUser();
        lblResult.setText(String.format("Korisnik nađen: %s %s.", user.getFirstName(), user.getLastName()));

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
