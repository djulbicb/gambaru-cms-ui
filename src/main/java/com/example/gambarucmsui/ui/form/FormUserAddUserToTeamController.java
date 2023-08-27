package com.example.gambarucmsui.ui.form;

import com.example.gambarucmsui.database.entity.TeamEntity;
import com.example.gambarucmsui.ports.Container;
import com.example.gambarucmsui.ports.ValidatorResponse;
import com.example.gambarucmsui.ports.interfaces.barcode.BarcodeLoadPort;
import com.example.gambarucmsui.ports.interfaces.user.IsUserAlreadyInThisTeamPort;
import com.example.gambarucmsui.ports.interfaces.team.TeamLoadPort;
import com.example.gambarucmsui.ports.interfaces.user.UserAddToTeamPort;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

import static com.example.gambarucmsui.database.entity.BarcodeEntity.BARCODE_ID;
import static com.example.gambarucmsui.util.FormatUtil.parseBarcodeStr;
import static com.example.gambarucmsui.util.LayoutUtil.getOr;

public class FormUserAddUserToTeamController implements Initializable {
    private final Long userId;
    private final UserAddToTeamPort userAddToTeamPort;
    private final TeamLoadPort teamLoadPort;
    private final BarcodeLoadPort barcodeLoadPort;
    private final IsUserAlreadyInThisTeamPort isUserInTeam;

    @FXML private VBox root;
    @FXML private Label lblErrUserBarcodeId;
    @FXML private Label lblErrUserTeamName;
    @FXML private ComboBox<String> cmbUserTeamName;
    @FXML private TextField txtUserBarcodeId;

    @FXML private CheckBox chkPaySubscription;

    @FXML private CheckBox chkSubscriptionFree;

    public FormUserAddUserToTeamController(Long userId) {
        this.userId = userId;
        this.userAddToTeamPort = Container.getBean(UserAddToTeamPort.class);
        this.teamLoadPort = Container.getBean(TeamLoadPort.class);
        this.barcodeLoadPort = Container.getBean(BarcodeLoadPort.class);
        this.isUserInTeam = Container.getBean(IsUserAlreadyInThisTeamPort.class);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        List<TeamEntity> allTeams = teamLoadPort.findAllActive();
        for (TeamEntity team : allTeams) {
            cmbUserTeamName.getItems().add(team.getName());
        }
    }

    @FXML
    void toggleBarcodeUnlock(MouseEvent event) {
        txtUserBarcodeId.setDisable(!txtUserBarcodeId.isDisabled());
    }
    @FXML
    void onAddUserToTeam(MouseEvent event) {
        String barcodeIdStr = getOr(txtUserBarcodeId, "");
        String teamNameStr = getOr(cmbUserTeamName, "");

        if (validate(userId, barcodeIdStr, teamNameStr)) {
            Long barcodeId = parseBarcodeStr(barcodeIdStr);
            String teamName = teamNameStr;

            LocalDate start = null;
            LocalDate end = null;
            if (chkPaySubscription.isSelected()) {
                start = LocalDate.now();
                end = start.plusMonths(1);
            }

            userAddToTeamPort.verifyAndAddUserToPort(userId, String.valueOf(barcodeId), teamName, chkSubscriptionFree.isSelected(), start, end);
            close();
        }
    }

    private boolean validate(Long userId, String barcodeId, String teamName) {
        ValidatorResponse validator = userAddToTeamPort.verifyAddUserToPort(userId, barcodeId, teamName, chkSubscriptionFree.isSelected(), null, null);

        if (validator.hasErrors()) {
            lblErrUserBarcodeId.setText(validator.getErrorOrEmpty(BARCODE_ID));
            lblErrUserTeamName.setText(validator.getErrorOrEmpty(TeamEntity.TEAM_NAME));
            return false;
        }
        return true;
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

    @FXML
    void onFreeOfChargeClick(ActionEvent e) {
        if (chkSubscriptionFree.isSelected()) {
            chkPaySubscription.setSelected(false);
            chkPaySubscription.setDisable(true);
        } else {
            chkPaySubscription.setDisable(false);
        }
    }

    public void onBarcodeScanned(String numberOnly) {
        txtUserBarcodeId.setText(numberOnly);
        lblErrUserBarcodeId.setText("");
    }

    private void close() {
        root.getScene().getWindow().hide();
    }
}

