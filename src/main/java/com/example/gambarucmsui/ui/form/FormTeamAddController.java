package com.example.gambarucmsui.ui.form;

import com.example.gambarucmsui.database.entity.TeamEntity;
import com.example.gambarucmsui.ports.ValidatorResponse;
import com.example.gambarucmsui.ports.interfaces.team.TeamIfExists;
import com.example.gambarucmsui.ports.interfaces.team.TeamSavePort;
import com.example.gambarucmsui.ui.ToastView;
import com.example.gambarucmsui.ui.form.validation.TeamInputValidator;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

import static com.example.gambarucmsui.util.LayoutUtil.getOr;

public class FormTeamAddController implements Initializable {
    // Repo
    //////////////////////////////////////////
    private TeamInputValidator validator = new TeamInputValidator();
    private final TeamIfExists teamIfExists;
    private final TeamSavePort teamSavePort;
    // FXML
    //////////////////////////////////////////
    @FXML private VBox root;
    @FXML private Label lblErrMembershipFee;
    @FXML private Label lblErrTeamName;
    @FXML private TextField txtMembershipFee;
    @FXML private TextField txtTeamName;


    public FormTeamAddController(TeamIfExists teamIfExists, TeamSavePort teamSavePort) {
        this.teamIfExists = teamIfExists;
        this.teamSavePort = teamSavePort;
    }

    @FXML
    void txtMembershipFeeReset(KeyEvent event) {
        lblErrMembershipFee.setText("");
    }

    @FXML
    void txtTeamNameReset(KeyEvent event) {
        lblErrTeamName.setText("");
    }

    @FXML
    void onClose(MouseEvent event) {
        close();
    }

    @FXML
    void onSave(MouseEvent event) {
        String paymentFeeStr = getOr(txtMembershipFee, "");
        String teamNameStr = getOr(txtTeamName, "");

        ValidatorResponse res = teamSavePort.verifyAndSaveTeam(teamNameStr, paymentFeeStr);

        if (res.hasErrors()) {
            lblErrTeamName.setText(res.getErrorOrEmpty(TeamEntity.TEAM_NAME));
            lblErrMembershipFee.setText(res.getErrorOrEmpty(TeamEntity.MEMBERSHIP_PAYMENT));
            return;
        }

        ToastView.showModal(res.getMessage());
        close();
    }

    @FXML
    private void close() {
        root.getScene().getWindow().hide();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

}
