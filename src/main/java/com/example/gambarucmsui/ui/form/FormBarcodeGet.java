package com.example.gambarucmsui.ui.form;

import com.example.gambarucmsui.adapter.out.persistence.entity.BarcodeEntity;
import com.example.gambarucmsui.adapter.out.persistence.entity.UserEntity;
import com.example.gambarucmsui.adapter.out.persistence.repo.BarcodeRepository;
import com.example.gambarucmsui.util.FormatUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

import java.util.Optional;

import static com.example.gambarucmsui.util.FormatUtil.*;

public class FormBarcodeGet {

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

    public FormBarcodeGet(BarcodeRepository barcodeRepo) {
        this.barcodeRepo = barcodeRepo;
    }

    @FXML void onOk(MouseEvent event) {
        String barcodeId = txtBarcodeId.getText().trim();
        if (barcodeId.isBlank()) {
            lblErrBarcodeId.setText("Upiši barkod.");
            return;
        }
        isFormReady = true;
        outBarcodeId = parseBarcodeStr(barcodeId);

        close();
    }
    @FXML void onClose(MouseEvent event) {
        close();
    }
    @FXML
    void onTextfieldTyped(KeyEvent event) {
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
        if (barcodeEntityOptional.isEmpty()) { return; }

        BarcodeEntity b = barcodeEntityOptional.get();
        if (b.getStatus() != BarcodeEntity.Status.ASSIGNED || b.getUser() == null) {
            return;
        }
        UserEntity user = b.getUser();
        lblResult.setText(String.format("Korisnik %s %s.", user.getFirstName(), user.getLastName()));
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
