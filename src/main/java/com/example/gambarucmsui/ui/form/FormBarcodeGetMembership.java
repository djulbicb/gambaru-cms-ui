package com.example.gambarucmsui.ui.form;

import com.example.gambarucmsui.database.entity.BarcodeEntity;
import com.example.gambarucmsui.database.entity.PersonEntity;
import com.example.gambarucmsui.ports.Container;
import com.example.gambarucmsui.ports.ValidatorResponse;
import com.example.gambarucmsui.ports.interfaces.barcode.BarcodeLoadPort;
import com.example.gambarucmsui.ports.interfaces.membership.AddUserMembership;
import com.example.gambarucmsui.ports.interfaces.user.UserLoadPort;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

import java.time.LocalDate;

import static com.example.gambarucmsui.database.entity.BarcodeEntity.BARCODE_ID;
import static com.example.gambarucmsui.util.FormatUtil.*;
import static com.example.gambarucmsui.util.LayoutUtil.getOr;

public class FormBarcodeGetMembership {

    // PORTS
    //////////////////////////////////////////
    private final AddUserMembership addUserMembership;
    private final LocalDate inCurrentDate;
    private final UserLoadPort userLoadPort;
    private final BarcodeLoadPort barcodeLoad;

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

    public FormBarcodeGetMembership(LocalDate timestamp) {
        addUserMembership = Container.getBean(AddUserMembership.class);
        userLoadPort = Container.getBean(UserLoadPort.class);
        barcodeLoad = Container.getBean(BarcodeLoadPort.class);
        this.inCurrentDate = timestamp;
    }

    @FXML void onOk(MouseEvent event) {
        String barcodeIdStr = getOr(txtBarcodeId, "");
        int month = inCurrentDate.getMonthValue();
        int year = inCurrentDate.getYear();

        if (validate(barcodeIdStr)) {
            addUserMembership.validateAndAddMembership(barcodeIdStr, inCurrentDate.atStartOfDay());
            close();
        }
    }

    boolean validate(String barcodeIdStr) {
        int month = inCurrentDate.getMonthValue();
        int year = inCurrentDate.getYear();
        ValidatorResponse validator = addUserMembership.velidateAddMembership(barcodeIdStr, inCurrentDate.atStartOfDay());

        if (validator.hasErrors()) {
            lblErrBarcodeId.setText(validator.getErrorOrEmpty(BARCODE_ID));
            return false;
        }

        PersonEntity user = userLoadPort.findUserByBarcodeId(parseBarcodeStr(barcodeIdStr)).get();
        lblResult.setText(String.format("Polaznik: %s %s", user.getFirstName(), user.getLastName()));
        return true;
    }

    @FXML void onClose(MouseEvent event) {
        close();
    }
    @FXML
    void onBarcodeIdTyped(KeyEvent event) {
        lblErrBarcodeId.setText("");
        lblResult.setText("");
        txtTeam.setText("");
        txtMembershipFee.setText("");

        String barcodeIdStr = getOr(txtBarcodeId, "");
        if (validate(barcodeIdStr)) {
            BarcodeEntity barcode = barcodeLoad.findById(parseBarcodeStr(barcodeIdStr)).get();
            txtTeam.setText(barcode.getTeam().getName());
            txtMembershipFee.setText(String.valueOf(barcode.getTeam().getMembershipPayment()));
        }
    }

    private void close() {
        root.getScene().getWindow().hide();
    }

}
